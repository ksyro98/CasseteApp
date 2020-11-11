package com.syroniko.casseteapp.StartingActivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.utils.CLIENT_ID
import com.syroniko.casseteapp.utils.REDIRECT_URI
import com.syroniko.casseteapp.utils.SPOTIFY_REQUEST_CODE
import com.syroniko.casseteapp.utils.onSpotifyResponse


val TAG: String = SplashActivity::class.java.simpleName

class SplashActivity: AppCompatActivity() {

    private var splashScreenDuration = 500L
    private val uid = Auth.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (uid != null) splashScreenDuration = 0

        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request)
    }

    private fun scheduleSplashScreen(spotifyToken: String) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                // After the splash screen duration, route to the right activities

                if (uid == null){
                   WelcomingActivity.startActivity(this, spotifyToken)
                    finish()
                }
                else{
                    UserDB.getDocumentFromId(uid)
                        .addOnSuccessListener { documentSnapshot ->
                            val user = documentSnapshot.toObject(User::class.java)
                            MainActivity.startActivity(this, uid, user, spotifyToken)
                            finish()
                        }
                }

            },
            splashScreenDuration
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPOTIFY_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            onSpotifyResponse(response, ::scheduleSplashScreen)
        }
    }
}