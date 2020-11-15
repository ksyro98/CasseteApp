package com.syroniko.casseteapp.chatAndMessages

import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.firebase.ChatDB
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
    getTime(scope) { timeSent ->

        val message = Message(
            senderUid,
            receiverUid,
            text,
            timeSent,
            false,
            chatId + timeSent.toString()
        )

        ChatDB.update(
            chatId, hashMapOf(
                Pair("messages", FieldValue.arrayUnion(message)),
                Pair("lastMessageSent", message.timestamp)
            )
        )
    }
}

fun sendFirstMessage(senderUid: String, receiverUid: String, text: String, scope: CoroutineScope){
    val chatId = getChatIdFromUids(senderUid, receiverUid)

    getTime(scope) { timeSent ->
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
        UserDB.update(
            senderUid, hashMapOf(
                Pair("friends", FieldValue.arrayUnion(receiverUid)),
                Pair("friends", FieldValue.arrayUnion(senderUid))
            )
        )
    }
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

@Throws(Exception::class)
fun getTime(scope: CoroutineScope, callback: (Long) -> Unit) {

    scope.launch(Dispatchers.IO) {
        val url = "https://time.is/Unix_time_now"
        val doc: Document = Jsoup.parse(URL(url).openStream(), "UTF-8", url)
        val tags = arrayOf(
            "div[id=time_section]",
            "div[id=clock0_bg]"
        )
        var elements: Elements = doc.select(tags[0])
        for (i in tags.indices) {
            elements = elements.select(tags[i])
        }

        callback((elements.text().toString() + "000").toLong())
    }

}