package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.syroniko.casseteapp.chatAndMessages.Chat
import com.syroniko.casseteapp.chatAndMessages.entities.Message
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage
import com.syroniko.casseteapp.firebasefirebase.CHATS
import com.syroniko.casseteapp.firebasefirebase.MESSAGES

object ChatDB: FirestoreDB(CHATS) {

//    override fun insert(item: Any) {
//        dbCollection.
//    }

    private lateinit var chatRegistration: ListenerRegistration

    override fun insertWithId(id: String, item: Any) {
        if (item !is Chat){
            return
        }

        val itemToAdd = ChatDocument(item)

        dbCollection.document(id).set(itemToAdd)
            .addOnSuccessListener {
                val collection = dbCollection.document(id).collection(MESSAGES)
                item.messages.map { message ->
                    collection.document(message.messageId).set(message)
                }
            }
    }

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

    private class ChatDocument(chat: Chat){
        var uids: ArrayList<String> = arrayListOf()
        var lastMessageSentAt: Long = 0
        var id: String = ""
        var lastMessageSent: Message = TextMessage()

        init{
            this.uids = chat.uids
            this.lastMessageSentAt = chat.lastMessageSentAt
            this.id = chat.id
            this.lastMessageSent = chat.lastMessageSent
        }
    }
}