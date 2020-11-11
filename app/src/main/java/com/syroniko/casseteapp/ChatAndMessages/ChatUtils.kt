package com.syroniko.casseteapp.ChatAndMessages

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.MainClasses.longToast
import com.syroniko.casseteapp.firebase.ChatDB
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.UserAndTime
import kotlinx.coroutines.launch
import java.security.MessageDigest
import kotlin.collections.ArrayList

//fun sendMessage(context: Context, sender: String, receiver: String, text: String, time: String, db: FirebaseFirestore): Unit{
//    val user: MutableMap<String, Any> = HashMap()
//    if (sender.hashCode() > receiver.hashCode()) {
//        user["chatid"] = sender + receiver
//    }
//    else {
//        user["chatid"] = receiver + sender
//    }
//
//    user["receiver"] = receiver
//    user["sender"] = sender
//    user["isseen"] = false
//    user["timestamp"] = time
//
//
//    user["message"] = text
//
//
//    db.collection("chats")
//        .add(user)
//        .addOnSuccessListener { documentReference ->
//            Log.d(
//                "TAG",
//                "DocumentSnapshot added with ID: " + documentReference.id
//            )
//        }
//        .addOnFailureListener { e -> Log.w("TAG", "Error adding document", e) }
//
//    val messageTime = System.currentTimeMillis()
//    updateUserTimes(db, sender, receiver, messageTime)
//    updateUserTimes(db, receiver, sender, messageTime)
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////PART OF REMOVING ROOM FROM THE APP, DON'T DELETE UNTIL WE MAKE SURE THAT IT'S A GOOD IDEA///////
////    updateRoom(context, sender, messageTime)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//    //      seenMessage(uid);
//
//}

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

fun updateUserTimes(db: FirebaseFirestore, uid1: String, uid2: String, messageTime: Long){
    db.collection("users")
        .document(uid1)
        .get()
        .addOnSuccessListener { documentSnapshot ->
            val tempUser =
                documentSnapshot.toObject(
                    User::class.java
                )
            val m = tempUser!!.friends

//            m[uid2] = messageTime
            db.collection("users")
                .document(uid1)
                .update("friends", m)
        }
}

fun updateRoom(activity: Activity?, sender: String, messageTime: Long){
    if (activity == null){
        return
    }

    val localDb = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, "user_time_database").build()

    val lifecycleScope =
        when (activity) {
            is AppCompatActivity -> {
                activity.lifecycleScope
            }
            is FragmentActivity -> {
                activity.lifecycleScope
            }
            else -> {
                null
            }
        }

    if (lifecycleScope == null){
        activity.longToast("There was an error while storing your data in your phone.")
        Log.e("ChatUtils", "lifecycleScope is null")
    }

    var userAndTime = UserAndTime()
    lifecycleScope!!.launch {
        userAndTime = localDb.userTimeDao().get(sender)
    }
    //TODO this code needs to be added somehow but currently it causes a crash related with the co-routine
//        .invokeOnCompletion {
//            userAndTime.time = messageTime
//            lifecycleScope.launch {
//                localDb.userTimeDao().update(userAndTime)
//            }
//        }


}

fun insertInLocalDb(fragment: Fragment, list: java.util.ArrayList<UserAndTime>){
    if(fragment.context == null){
        return
    }

    val localDb = Room.databaseBuilder(fragment.context as Context, AppDatabase::class.java, "user_time_database").build()

    fragment.lifecycleScope.launch {
        list.map { ut ->
            localDb.userTimeDao().insertAll(ut)
        }
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