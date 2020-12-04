package com.syroniko.casseteapp.utils

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.messaging.FirebaseMessaging
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.User

const val FCM_NO_TOKEN = "fcm_no_token"
private const val TAG = "FCMUtils"

private fun getFCMToken(onSuccess: (String) -> Unit){
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful){
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result

        onSuccess(token)
    }
}

fun addFCMToken(uid: String){
    getFCMToken { token ->
        UserDB.update(uid, hashMapOf(Pair("fcmTokens", FieldValue.arrayUnion(token))))
    }
}

fun addFCMTokenWhenNeeded(user: User){
    getFCMToken { token ->
        if (user.uid != null && !user.fcmTokens.contains(token)){
            UserDB.update(user.uid!!, hashMapOf(Pair("fcmTokens", FieldValue.arrayUnion(token))))
        }
    }
}

