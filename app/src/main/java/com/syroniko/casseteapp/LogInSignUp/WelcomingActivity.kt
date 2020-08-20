package com.syroniko.casseteapp.LogInSignUp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.syroniko.casseteapp.R
import kotlinx.android.synthetic.main.activity_welcoming.*

class WelcomingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcoming)

        val tx = findViewById<TextView>(R.id.app_quote_textview)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont


        loginButWelcAct.setOnClickListener {
            LoginActivity.startActivity(this)
            finish()
        }

        signUpButWelcAct.setOnClickListener {
            SignUpActivity.startActivity(this)
            finish()
        }
    }

    companion object {
        fun startActivity(context: Context){
            val intent = Intent(context, WelcomingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}