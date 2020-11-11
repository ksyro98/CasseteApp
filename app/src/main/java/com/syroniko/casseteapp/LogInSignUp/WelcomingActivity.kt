package com.syroniko.casseteapp.LogInSignUp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.utils.SPOTIFY_NO_TOKEN
import kotlinx.android.synthetic.main.activity_welcoming.*

class WelcomingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcoming)

        val tx = findViewById<TextView>(R.id.app_quote_textview)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont

        val spotifyToken = intent.getStringExtra(SPOTIFY_TOKEN_EXTRA_NAME) ?: SPOTIFY_NO_TOKEN

        loginButWelcAct.setOnClickListener {
            LoginActivity.startActivity(this, spotifyToken)
            finish()
        }

        signUpButWelcAct.setOnClickListener {
            SignUpActivity.startActivity(this, spotifyToken)
            finish()
        }
    }

    companion object {
        private const val SPOTIFY_TOKEN_EXTRA_NAME = "spotify token extra name"

        fun startActivity(context: Context, spotifyToken: String){
            val intent = Intent(context, WelcomingActivity::class.java)
            intent.putExtra(SPOTIFY_TOKEN_EXTRA_NAME, spotifyToken)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}