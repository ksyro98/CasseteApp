package com.syroniko.casseteapp.startingActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.chatAndMessages.ChatActivity
import com.syroniko.casseteapp.chatAndMessages.DisplayedChat
import com.syroniko.casseteapp.logInSignUp.WelcomingActivity
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.utils.*
import java.lang.Long.parseLong


val TAG: String = SplashActivity::class.java.simpleName

class SplashActivity: AppCompatActivity() {

    private var splashScreenDuration = 500L
    private val uid = Auth.getUid()
    private lateinit var user: User
    private lateinit var displaedChat: DisplayedChat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        apply{
            val senderId = intent.getStringExtra("senderId") ?: return@apply
            val senderName = intent.getStringExtra("senderName") ?: return@apply
            val senderStatus = intent.getStringExtra("senderStatus") ?: return@apply
            val lastMessageText = intent.getStringExtra("lastMessageText") ?: return@apply
            val timestamp = parseLong(intent.getStringExtra("timestamp") ?: return@apply)
            val chatId = intent.getStringExtra("chatId") ?: return@apply

            displaedChat = DisplayedChat(
                senderId,
                "",
                senderName,
                senderStatus,
                lastMessageText,
                lastMessageRead = false,
                lastMessageSentByMe = false,
                timestamp = timestamp,
                chatId = chatId
            )
        }

        loadAndStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPOTIFY_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            onSpotifyResponse(response) { token ->
                user.spotifyToken = token
                if (uid != null) {
                    UserDB.update(uid, hashMapOf(Pair("spotifyToken", token)))
                }
                MainActivity.startActivity(this, uid, user)
                finish()
            }
        }
    }

    private fun loadAndStart(){
        if (uid == null){
            WelcomingActivity.startActivity(this)
            finish()
        }
        else{
            UserDB.getDocumentFromId(uid)
                .addOnSuccessListener { documentSnapshot ->
                    user = documentSnapshot.toObject(User::class.java) ?: return@addOnSuccessListener
                    user.uid = uid
                    addFCMTokenWhenNeeded(user)
                    if (user.spotifyToken == SPOTIFY_NO_TOKEN){
                        getSpotifyToken()
                    }
                    else {
                        if(::displaedChat.isInitialized){
                            ChatActivity.startActivity(this, displaedChat, true)
                        }
                        else{
                            MainActivity.startActivity(this, uid, user)
                        }
                        finish()
                    }
                }
        }
    }

    private fun getSpotifyToken(){
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request)
    }
}