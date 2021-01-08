package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ListenerRegistration
import com.syroniko.casseteapp.chatAndMessages.Chat
import com.syroniko.casseteapp.chatAndMessages.DisplayedChat
import com.syroniko.casseteapp.chatAndMessages.entities.Message
import com.syroniko.casseteapp.chatAndMessages.entities.MessageType
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage
import com.syroniko.casseteapp.chatAndMessages.getTheOtherUid
import com.syroniko.casseteapp.firebasefirebase.CHATS
import com.syroniko.casseteapp.firebasefirebase.MESSAGES
import com.syroniko.casseteapp.mainClasses.User

object ChatDB: FirestoreDB(CHATS) {

//    override fun insert(item: Any) {
//        dbCollection.
//    }

    private lateinit var messageRegistration: ListenerRegistration
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

    fun insertMessageWithCallback(chatId: String, message: Message, callback: () -> Unit){
        dbCollection.document(chatId).collection(MESSAGES).document(message.messageId).set(message)
            .addOnSuccessListener {
                callback()
            }
    }

    fun getChatsThatIncludesUser(uid: String) = dbCollection.whereArrayContains("uids", uid).get()

    fun listenToMessages(chatId: String, onStart: () -> Unit, onChange: (Message) -> Unit) {
        val tag = ChatDB::class.java.simpleName

        messageRegistration = db.collection(CHATS).document(chatId).collection(MESSAGES)
            .addSnapshotListener { snapshot, e ->
                if(e != null){
                    Log.w(tag, "List failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    onStart()

                    snapshot.documents.map { document ->

                        val messageType = MessageType.getFromString(
                            document.getString("type") ?: ""
                        ) ?: return@addSnapshotListener
                        val message = document.toObject(messageType.getMessageClass())

                        onChange(message as Message)
                    }

                }
                else{
                    Log.d(tag, "Current data: null")
                }
            }
    }

    fun updateMessage(chatId: String, id: String, updateMap: HashMap<String, Any>) =
        dbCollection.document(chatId).collection(MESSAGES).document(id).update(updateMap)

    override fun getId(): String {
        return CHATS
    }

//    Old code, keep it until we're sure that the new code works
//    fun listenToChat(uid: String, onChange: (QuerySnapshot) -> Unit) {
//        val tag = FirestoreDB::class.java.simpleName
//
//        chatRegistration = db.collection(CHATS).whereArrayContains("uids", uid)
//            .addSnapshotListener { snapshot, e ->
//                if(e != null){
//                    Log.w(tag, "List failed.", e)
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null){
//                    onChange(snapshot)
//               }
//                else{
//                    Log.d(tag, "Current data: null")
//                }
//            }
//    }

    fun listenToChat(uid: String, onStart: () -> Unit, onChange: (DisplayedChat) -> Unit){
        val tag = FirestoreDB::class.java.simpleName

        chatRegistration = dbCollection
            .whereArrayContains("uids", uid).addSnapshotListener { snapshot, e ->
                if(e != null){
                    Log.w(tag, "List failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    onStart()

                    snapshot.documents.map { document ->
                        val id = document.id
                        val lastMessage = document.get("lastMessageSent") as HashMap<*, *>
                        val lastMessageType = MessageType.getFromString(lastMessage["type"].toString())
                        val uids = (document.get("uids") as ArrayList<*>)
                            .map { obj -> obj.toString() } as ArrayList<String>
                        val otherId = getTheOtherUid(uids, uid) ?: return@addSnapshotListener

                        UserDB.getDocumentFromId(otherId).addOnSuccessListener {
                            val user = it.toObject(User::class.java) ?: return@addOnSuccessListener

                            val displayedChat = DisplayedChat(
                                otherId,
                                user.image ?: "",
                                user.name ?: "",
                                user.status,
                                lastMessageType?.getMessageText(user.name ?: "") ?: lastMessage["text"].toString(),
                                lastMessage["read"] as Boolean,
                                lastMessage["senderId"].toString() == uid,
                                lastMessage["timestamp"] as Long,
                                id ?: ""
                            )

                            onChange(displayedChat)
                        }
                    }
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

    fun detachMessageListener(){
        if(::messageRegistration.isInitialized){
            messageRegistration.remove()
        }
    }

    //not ready yet
    fun getChat(chatId: String, callback: (Chat) -> Unit){
        getDocumentFromId(chatId).addOnSuccessListener { documentSnapshot ->
            val lastMessageMap = documentSnapshot["lastMessageSent"] as HashMap<*, *>
            val lastMessageType = MessageType.getFromString(lastMessageMap["type"].toString())


            val chat = Chat(
                (documentSnapshot["uids"] as ArrayList<*>).map { obj -> obj.toString() } as ArrayList<String>,
                arrayListOf(),
                documentSnapshot["lastMessageSentAt"] as Long,
                documentSnapshot.id
            )
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