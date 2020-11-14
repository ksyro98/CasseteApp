package com.syroniko.casseteapp.LogInSignUp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.utils.SPOTIFY_NO_TOKEN

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val nameEditText = findViewById<EditText>(R.id.name_et_sign_up)
        val emailET = findViewById<EditText>(R.id.email_et_signup1)
        val password = findViewById<EditText>(R.id.password_et_signup)
        val password2 = findViewById<EditText>(R.id.password_confirm_et_signup)
        val next = findViewById<TextView>(R.id.nextfromsignup_togenres)
        val tx = findViewById<TextView>(R.id.create_an_account_string)

        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont

        next.setOnClickListener {
            val pass = password.text.toString()

            if (nameEditText.text.isEmpty() || emailET.text.isEmpty() || pass.isEmpty()) {
                toast("Please fill all the info.")
            }
            else if (password.text.toString() != password2.text.toString()) {
                toast("Passwords don't match.")
            }
            else if (pass.length < 6) {
                toast("Password must have at least 6 characters.")
            }
            else {
                val userInstance = User(
                    name = nameEditText.text.toString(),
                    email = emailET.text.toString(),
                    status = "offline",
                    uid = null
                )

                PickGenresSignUpActivity.startActivity(this, userInstance, pass)
                finish()
            }
        }
    }

    companion object {
        fun startActivity(context: Context){
            val intent = Intent(context, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}