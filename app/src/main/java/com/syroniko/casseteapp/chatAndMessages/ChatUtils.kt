package com.syroniko.casseteapp.chatAndMessages

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.syroniko.casseteapp.chatAndMessages.entities.Message
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage
import com.syroniko.casseteapp.firebase.ChatDB
import com.syroniko.casseteapp.firebase.FirestoreDB
import com.syroniko.casseteapp.firebase.UserDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL
import java.security.MessageDigest
import kotlin.collections.ArrayList


fun sendMessage(senderUid: String, receiverUid: String, text: String, scope: CoroutineScope) {
    val chatId = getChatIdFromUids(senderUid, receiverUid)

    val timeSent = FirestoreDB.getTime()

    val message = TextMessage(
        Message.getMessageId(chatId, timeSent),
        senderUid,
        receiverUid,
        timeSent,
        false,
        text
    )

    ChatDB.update(
        chatId, hashMapOf(
            Pair("messages", FieldValue.arrayUnion(message)),
            Pair("lastMessageSentAt", message.timestamp),
            Pair("lastMessageSent", message)
        )
    )

}

fun sendFirstMessage(senderUid: String, receiverUid: String, text: String, scope: CoroutineScope){
    val chatId = getChatIdFromUids(senderUid, receiverUid)

    val timeSent = FirestoreDB.getTime()
    val message = TextMessage(
        Message.getMessageId(chatId, timeSent),
        senderUid,
        receiverUid,
        timeSent,
        false,
        text
    )

    val chat = Chat(
        arrayListOf(senderUid, receiverUid),
        arrayListOf(message),
        timeSent,
        chatId,
        message
    )

    ChatDB.insertWithId(chat.id, chat)
    UserDB.update(
        senderUid, hashMapOf(
            Pair("friends", FieldValue.arrayUnion(receiverUid)),
            Pair("friends", FieldValue.arrayUnion(senderUid))
        )
    )
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

//@Throws(Exception::class)
//fun getTime(scope: CoroutineScope, callback: (Long) -> Unit) {
//
//    val t = Timestamp.now().seconds
//    callback(t)
//
////    scope.launch(Dispatchers.IO) {
////        val url = "https://time.is/Unix_time_now"
////        val doc: Document = Jsoup.parse(URL(url).openStream(), "UTF-8", url)
////        val tags = arrayOf(
////            "div[id=time_section]",
////            "div[id=clock0_bg]"
////        )
////        var elements: Elements = doc.select(tags[0])
////        for (i in tags.indices) {
////            elements = elements.select(tags[i])
////        }
////
////        callback((elements.text().toString() + "000").toLong())
////    }
//
//}
