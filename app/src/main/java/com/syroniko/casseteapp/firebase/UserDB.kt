package com.syroniko.casseteapp.firebase

import android.util.Log
import com.syroniko.casseteapp.logInSignUp.CountrySelectSignUpActivity
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.firebasefirebase.USERS
import com.syroniko.casseteapp.utils.addFCMToken

const val STATUS_ONLINE = "online"
const val STATUS_OFFLINE = "offline"

object UserDB: FirestoreDB(USERS) {

    override fun insert(item: Any) {
        if (item !is User || item.uid == null) {
            return
        }
        dbCollection.document(item.uid!!).set(item)
            .addOnSuccessListener {
                Log.d(CountrySelectSignUpActivity::class.java.simpleName, "success")
                addFCMToken(item.uid!!)
            }
    }

    @Deprecated("This function should no longer be used. Use getUsersForPossibleReceivers instead.")
    fun getPossibleReceivers(
        genre: String,
        restrictedReceivers: ArrayList<String?>,
        successCallback: (ArrayList<String>) -> Unit
    ) {
        dbCollection
            .orderBy("receivedLastCassetteAt")
            .whereArrayContains("genres", genre)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val possibleReceivers = arrayListOf<String>()

                documents.map { document ->
                    val userId = document.data["uid"].toString()
                    if (!restrictedReceivers.contains(userId)) {
                        possibleReceivers.add(userId)
                    }
                }

                successCallback(possibleReceivers)

            }
            .addOnFailureListener { e ->
                Log.e(UserDB::class.java.simpleName, "Retrieving Data Error", e)
            }
    }

    fun getUsersForPossibleReceivers(
        genre: String,
        restrictedReceivers: ArrayList<String?>,
        successCallback: (ArrayList<User>) -> Unit
    ){
        dbCollection
            .orderBy("receivedLastCassetteAt")
            .whereArrayContains("genres", genre)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val possibleReceivers = arrayListOf<User>()

                documents.map { document ->
                    val userId = document.data["uid"].toString()
                    if (!restrictedReceivers.contains(userId)){
                        possibleReceivers.add(document.toObject(User::class.java))
                    }
                }

                successCallback(possibleReceivers)

            }
            .addOnFailureListener { e ->
                Log.e(UserDB::class.java.simpleName, "Retrieving Data Error", e)
            }
    }

    fun getFriends(
        successCallback: (ArrayList<*>) -> Unit
    ) {
        UserDB.getDocumentFromId(Auth.getUid().toString())
            .addOnSuccessListener { documentSnapshot ->
                val userFriends = documentSnapshot["friends"] as ArrayList<*>
                successCallback(userFriends)
            }

    }


    override fun getId(): String {
        return USERS
    }
}
