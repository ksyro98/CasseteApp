package com.syroniko.casseteapp.ChatAndMessages

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.MainClasses.longToast
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.UserAndTime
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

fun sendMessage(activity: Activity?, sender: String, receiver: String, text: String, time: String, db: FirebaseFirestore): Unit{
    val user: MutableMap<String, Any> = HashMap()
    if (sender.hashCode() > receiver.hashCode()) {
        user["chatid"] = sender + receiver
    }
    else {
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

    val messageTime = System.currentTimeMillis()
    updateUserTimes(db, sender, receiver, messageTime)
    updateUserTimes(db, receiver, sender, messageTime)
    updateRoom(activity, sender, messageTime)

    //      seenMessage(uid);

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

            m[uid2] = messageTime
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