package com.syroniko.casseteapp.LogInSignUp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.AuthCallback
import com.syroniko.casseteapp.firebase.UserDB
import com.ybs.countrypicker.CountryPicker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountrySelectSignUpActivity : AppCompatActivity() {

    //todo add auth functionality to a view model
    private val auth = Auth()
    @Inject lateinit var userDb: UserDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_select_sign_up)

        val locationTextView = findViewById<TextView>(R.id.location_text_view)
        val finishTextView = findViewById<TextView>(R.id.finsihTextView)
        val countryTextView = findViewById<TextView>(R.id.countryTextView)
        val tx = findViewById<TextView>(R.id.pick_country_string)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont

        val user = intent.getParcelableExtra<User>(COUNTRY_USER_EXTRA) ?: return
        val password = intent.getStringExtra(COUNTRY_PASSWORD_EXTRA) ?: return

        locationTextView.setOnClickListener {
            val picker = CountryPicker.newInstance("Select Country")

            picker.setListener { name, _, _, _ ->
                countryTextView.text = name
                user.country = name
                picker.dismiss()
            }

            picker.show(supportFragmentManager, "COUNTRY_PICKER")
        }

        finishTextView.setOnClickListener {
            if (user.country != null) {
                auth.signUpWithEmail(
                    user.email,
                    password,
                    object : AuthCallback {
                        override fun onSuccess(uid: String?) {
                            toast("Sign Up Completed.")

                            if (uid != null) {
                                Log.d(CountrySelectSignUpActivity::class.java.simpleName, "123")
                                user.uid = uid
                                Log.d(CountrySelectSignUpActivity::class.java.simpleName, uid)
                                userDb.insert(user)
////                                TODO("What is that? :O")
//                                val resultIntent = Intent()
//                                setResult(Activity.RESULT_OK, resultIntent)
//                                finish()
                                MainActivity.startActivity(this@CountrySelectSignUpActivity, uid)
                            }

                        }

                        override fun onFailure() {
                            toast("Sign Up Failed. Please try again.")
                        }
                    }
                )

            }
            else {
                toast("Please select a country.")
            }
        }
    }

    companion object {
        const val COUNTRY_USER_EXTRA = "genres user extra"
        const val COUNTRY_PASSWORD_EXTRA = "genres password extra"

        fun startActivity(context: Context, user: User, password: String){
            val intent = Intent(context, CountrySelectSignUpActivity::class.java)
            intent.putExtra(COUNTRY_USER_EXTRA, user)
            intent.putExtra(COUNTRY_PASSWORD_EXTRA, password)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}