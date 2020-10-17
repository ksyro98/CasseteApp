package com.syroniko.casseteapp.viewCassette

import android.app.Application
import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.syroniko.casseteapp.ChatAndMessages.sendMessage
import com.syroniko.casseteapp.TrackSearchFlow.NO_PREVIEW_URL
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.UserDB
import javax.inject.Inject

class CassetteViewerViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application)  {

    var trackPreviewUrl = NO_PREVIEW_URL
    lateinit var cassetteId: String
    lateinit var senderId: String
    lateinit var cassetteComment: String
    lateinit var trackName: String
    lateinit var trackId: String
    lateinit var senderName: String
    private val cassetteDB = CassetteDB()
    private val userDB = UserDB()
    private val uid = Auth.getUid()

    fun getCassette(callback: (String, String, String, String) -> Unit){
        cassetteDB.getDocumentFromId(cassetteId)
            .addOnSuccessListener { document ->
                cassetteComment = document.data?.get("comment") as String

                val trackMap = document.data?.get("track") as Map<*, *>
                trackName = trackMap["trackName"] as String
                trackId = trackMap["trackId"] as String
                if(trackMap["previewUrl"] != null) {
                    trackPreviewUrl = trackMap["previewUrl"] as String
                }

                callback(cassetteComment, trackName, trackId, trackPreviewUrl)
            }
    }

    fun getSender(callback: (String) -> Unit){
        userDB.getDocumentFromId(senderId)
            .addOnSuccessListener { document ->
                senderName = document.data?.get("name") as String
                callback(senderName)
            }
    }

    fun updateOnForward(){
        if (uid == null) {
            Log.e(CassetteViewerViewModel::class.simpleName, "uid is null")
            return
        }

        val updateMap = hashMapOf<String, Any>(
            Pair("possibleReceivers", FieldValue.arrayRemove(uid)),
            Pair("restrictedReceivers", FieldValue.arrayUnion(uid))
        )
        cassetteDB.update(cassetteId, updateMap)

        cassetteDB.update(cassetteId, hashMapOf(Pair("received", false)))

        userDB.update(uid, hashMapOf(Pair("cassettes", FieldValue.arrayRemove(cassetteId))))
    }

    fun updateOnReply(){
        if (uid == null) {
            Log.e(CassetteViewerViewModel::class.simpleName, "uid is null")
            return
        }

        userDB.update(uid, hashMapOf(Pair("friends", FieldValue.arrayUnion(senderId))))
        userDB.update(uid, hashMapOf(Pair("cassettes", FieldValue.arrayRemove(cassetteId))))
    }

    fun sendReplyMessage(){
        if (uid == null) {
            Log.e(CassetteViewerViewModel::class.simpleName, "uid is null")
            return
        }

        //TODO change that to server time
        //TODO change the way this (the db) is done in chat
        sendMessage(
            getApplication(),
            uid,
            senderId,
            "Hello, I received your cassette with $trackName and I loved it!",
            System.currentTimeMillis().toString(),
            Firebase.firestore)
    }

}