package com.syroniko.casseteapp.StartingActivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.R

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler().postDelayed(
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

        val intent = Intent(this, WelcomingActivity::class.java)
        when {


            forevertrue->startActivity(intent)

        }
    }

}