package com.syroniko.casseteapp.LogInSignUp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.AuthCallback
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    //TODO use hilt here with provider or it'll crash
    private lateinit var auth: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val tx = findViewById<TextView>(R.id.recoveraccounString_resect_act)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont

        resetEmailEditText.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            val colorStateList = ColorStateList.valueOf(Color.parseColor("#0fa59b"))
            ViewCompat.setBackgroundTintList(resetEmailEditText, colorStateList)
        }

        sendResetPassTextView.setOnClickListener {
            val userEmail = resetEmailEditText.text.toString()

            if (userEmail.isEmpty()) {
                toast("Please fill your email address.")
                return@setOnClickListener
            }

            auth.resetPassword(userEmail,
                object : AuthCallback {
                    override fun onSuccess(uid: String?) {
                        toast("Email sent. Check your inbox!")
                    }

                    override fun onFailure() {
                        toast("A problem occurred while sending your email. Please try again.")
                    }
                }
            )
        }
    }

    companion object{
        fun startActivity(context: Context){
            val i = Intent(context, ResetPasswordActivity::class.java)
            context.startActivity(i)
        }
    }
}