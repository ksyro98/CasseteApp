package com.syroniko.casseteapp.MainClasses

import android.app.Application
import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.UserDB
import javax.inject.Inject

private const val THIRTY_MINS = 1800000

class MainViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    var uid = Auth.getUid() ?: ""
    var token = ""
    val cassettes: MutableLiveData<MutableList<Cassette>> by lazy {
        MutableLiveData<MutableList<Cassette>>()
    }
    var user: User = User()
        set(value) {
            field = value

            getNewCassette()
        }


    init {
        retrieveCassettes()
    }

    private fun retrieveCassettes(){
        cassettes.value = mutableListOf()

        UserDB.getDocumentFromId(uid)
            .addOnSuccessListener { documentSnapshot ->
                val userCassettes = documentSnapshot["cassettes"] as ArrayList<*>

                for (userCassette in userCassettes) {
                    CassetteDB.getDocumentFromId(userCassette.toString())
                        .addOnSuccessListener { cassetteDocument ->
                            val cassette = cassetteDocument.toObject(Cassette::class.java)
                            cassette?.setId(cassetteDocument.id)

                            if (cassette != null) cassettes.addAndUpdate(cassette)
                        }
                }
            }
    }


    private fun getNewCassette() {
        if(System.currentTimeMillis() - user.receivedLastCassetteAt < THIRTY_MINS){
            return
        }

        CassetteDB.getOneCassetteForUser(uid)
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val newCassette = document.toObject(Cassette::class.java)
                    newCassette.setId(document.id)

                    cassettes.addAndUpdate(newCassette)

                    CassetteDB.update(newCassette.getId(), hashMapOf(Pair("received", true)))

                    UserDB.update(uid, hashMapOf(
                        Pair("cassettes", FieldValue.arrayUnion(newCassette.getId())),
                        Pair("cassettesAccepted", FieldValue.arrayUnion(newCassette.getId()))
                    ))

                    UserDB.update(uid, hashMapOf(Pair("receivedLastCassetteAt", System.currentTimeMillis())))
                }
            }
            .addOnFailureListener {
                Log.e(MainActivity::class.java.simpleName, "Retrieving Data Error", it)
            }
    }

}

fun MutableLiveData<MutableList<Cassette>>.addAndUpdate(item: Cassette){
    val tempValue = this.value
    tempValue?.add(item)
    this.value = tempValue
}