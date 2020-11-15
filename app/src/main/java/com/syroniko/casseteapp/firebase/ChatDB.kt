package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.syroniko.casseteapp.chatAndMessages.Chat
import com.syroniko.casseteapp.firebasefirebase.CHATS

object ChatDB: FirestoreDB(CHATS) {

    private lateinit var chatRegistration: ListenerRegistration

    fun insertWithId(chat: Chat) = dbCollection.document(chat.id).set(chat)

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

    fun listenToChat(uid: String, onChange: (QuerySnapshot) -> Unit) {
        val tag = FirestoreDB::class.java.simpleName

        chatRegistration = db.collection(CHATS).whereArrayContains("uids", uid)
            .addSnapshotListener { snapshot, e ->
                if(e != null){
                    Log.w(tag, "List failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    onChange(snapshot)
               }
                else{
                    Log.d(tag, "Current data: null")
                }
            }
    }

    fun detachChatListener(){
        if (::chatRegistration.isInitialized){
            chatRegistration.remove()
        }
    }

}