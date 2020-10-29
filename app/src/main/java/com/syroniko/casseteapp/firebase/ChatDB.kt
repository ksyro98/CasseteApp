package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestoreSettings
import com.syroniko.casseteapp.ChatAndMessages.Message
import com.syroniko.casseteapp.firebasefirebase.CHATS

class ChatDB: FirestoreDB(CHATS) {

//    init {
//        val settings = firestoreSettings {
//            isPersistenceEnabled = true
//        }
//        db.firestoreSettings = settings
//    }

    fun getChatsThatIncludesUser(uid: String) = dbCollection.whereArrayContains("uids", uid).get()

    fun listenToMessages(chatId: String, onChange: (DocumentSnapshot) -> Unit) {
        val tag = FirestoreDB::class.java.simpleName

        registration = db.collection(CHATS).document(chatId).addSnapshotListener { snapshot, e ->
            if(e != null){
                Log.w(tag, "List failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()){
                onChange(snapshot)
            }
            else{
                Log.d(tag, "Current data: null")
            }
        }
    }

    override fun getId(): String {
        return CHATS
    }


}