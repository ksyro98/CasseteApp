package com.syroniko.casseteapp.ChatAndMessages

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.MainClasses.User
import java.util.*

fun sendMessage(sender: String, receiver: String, text: String, time: String, db: FirebaseFirestore): Unit{
    val user: MutableMap<String, Any> =
        HashMap()
    if (sender.hashCode() > receiver.hashCode()) {
        user["chatid"] = sender + receiver
    } else {
        user["chatid"] = receiver + sender
    }

    user["receiver"] = receiver
    user["sender"] = sender
    user["isseen"] = false
    user["timestamp"] = time


    user["message"] = text


    db.collection("chats")
        .add(user)
        .addOnSuccessListener { documentReference ->
            Log.d(
                "TAG",
                "DocumentSnapshot added with ID: " + documentReference.id
            )
        }
        .addOnFailureListener { e -> Log.w("TAG", "Error adding document", e) }

    updateUserTimes(db, sender, receiver)
    updateUserTimes(db, receiver, sender)

    //      seenMessage(uid);

}

fun updateUserTimes(db: FirebaseFirestore, uid1: String, uid2: String){
    db.collection("users")
        .document(uid1)
        .get()
        .addOnSuccessListener { documentSnapshot ->
            val tempUser =
                documentSnapshot.toObject(
                    User::class.java
                )
            val m = tempUser!!.friends
            val messageTime = System.currentTimeMillis()
            m[uid2] = messageTime
            db.collection("users")
                .document(uid1)
                .update("friends", m)
        }
}