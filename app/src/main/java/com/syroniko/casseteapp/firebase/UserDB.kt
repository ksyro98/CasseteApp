package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.TrackSearchFlow.SendTrackActivity
import com.syroniko.casseteapp.firebasefirebase.USERS

class UserDB: FirestoreDB(USERS) {

    override fun insert(item: Any) {
        if (item !is User || item.uid == null){
            return
        }
        dbCollection.document(item.uid!!).set(item)
    }

    fun getPossibleReceivers(
        genre: String,
        restrictedReceivers: ArrayList<String?>,
        successCallback: (ArrayList<String>) -> Unit
    ){
        dbCollection
            .orderBy("receivedLastCassetteAt")
            .whereArrayContains("genres", genre)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val possibleReceivers = arrayListOf<String>()

                documents.map { document ->
                    val userId = document.data["uid"].toString()
                    if (!restrictedReceivers.contains(userId)){
                        possibleReceivers.add(userId)
                    }
                }

                successCallback(possibleReceivers)

            }
            .addOnFailureListener { e ->
                Log.e(UserDB::class.java.simpleName, "Retrieving Data Error", e)
            }
    }

    override fun getId(): String {
        return USERS
    }
}