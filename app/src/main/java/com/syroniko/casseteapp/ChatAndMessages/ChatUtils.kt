package com.syroniko.casseteapp.ChatAndMessages

import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.firebase.ChatDB
import com.syroniko.casseteapp.firebase.UserDB
import java.security.MessageDigest
import kotlin.collections.ArrayList


fun sendMessage(senderUid: String, receiverUid: String, text: String){
    val chatId = getChatIdFromUids(senderUid, receiverUid)

    val message = Message(
        senderUid,
        receiverUid,
        text,
        System.currentTimeMillis(),
        false,
        chatId + System.currentTimeMillis().toString()
    )

    ChatDB.update(chatId, hashMapOf(Pair("messages", FieldValue.arrayUnion(message))))
    ChatDB.update(chatId, hashMapOf(Pair("lastMessageSent", message.timestamp)))
}

fun sendFirstMessage(senderUid: String, receiverUid: String, text: String){
    val chatId = getChatIdFromUids(senderUid, receiverUid)
    val timeSent = System.currentTimeMillis()

    val message = Message(
        senderUid,
        receiverUid,
        text,
        timeSent,
        false,
        chatId + timeSent.toString()
    )

    val chat = Chat(
        arrayListOf(senderUid, receiverUid),
        arrayListOf(message),
        timeSent,
        chatId
    )

    ChatDB.insertWithId(chat)
    UserDB.update(senderUid, hashMapOf(Pair("friends", FieldValue.arrayUnion(receiverUid))))
    UserDB.update(receiverUid, hashMapOf(Pair("friends", FieldValue.arrayUnion(senderUid))))
}


fun getTheOtherUid(uids: ArrayList<String>, selectedUid: String): String?{
    for (uid in uids){
        if (uid != selectedUid){
            return uid
        }
    }
    return null
}

fun getChatIdFromUids(uid1: String, uid2: String): String {
    val commonId = if(uid1 > uid2) {
        uid1 + uid2
    }
    else {
        uid2 + uid1
    }
    val bytes = MessageDigest.getInstance("MD5").digest(commonId.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}