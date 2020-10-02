package com.syroniko.casseteapp.StartingActivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.MainClasses.thirtyMins
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebasefirebase.USERS

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                // After the splash screen duration, route to the right activities
                //val user = UserDb.getCurrentUser()
                val forevertrue=true
                routeToAppropriatePage(forevertrue)
                finish()
            },
            splashScreenDuration
        )
    }

    private fun getSplashScreenDuration() = 950L

    private fun routeToAppropriatePage(forevertrue:Boolean) {
        when {
            forevertrue->WelcomingActivity.startActivity(this)
        }
    }

}