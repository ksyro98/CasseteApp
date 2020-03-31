package com.syroniko.casseteapp.ChatAndMessages

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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

}