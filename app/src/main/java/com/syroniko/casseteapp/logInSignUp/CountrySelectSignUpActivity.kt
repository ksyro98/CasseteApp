package com.syroniko.casseteapp.logInSignUp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.AuthCallback
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.utils.*
import com.ybs.countrypicker.CountryPicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountrySelectSignUpActivity : AppCompatActivity() {

    private val auth = Auth()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_select_sign_up)

        val locationTextView = findViewById<TextView>(R.id.location_text_view)
        val finishTextView = findViewById<TextView>(R.id.finsihTextView)
        val countryTextView = findViewById<TextView>(R.id.countryTextView)
        val tx = findViewById<TextView>(R.id.pick_country_string)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont

        user = intent.getParcelableExtra(COUNTRY_USER_EXTRA) ?: return
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
                                user.uid = uid
                                UserDB.insert(user)
                                val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                                val request = builder.build()
                                AuthenticationClient.openLoginActivity(this@CountrySelectSignUpActivity, SPOTIFY_REQUEST_CODE, request)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPOTIFY_REQUEST_CODE){
            val response = AuthenticationClient.getResponse(resultCode, data)
            onSpotifyResponse(response) { token ->
                user.spotifyToken = token
                UserDB.insert(user)
                MainActivity.startActivity(this, user.uid, user)
                finish()
            }
        }
    }

    companion object {
        private const val COUNTRY_USER_EXTRA = "genres user extra"
        private const val COUNTRY_PASSWORD_EXTRA = "genres password extra"

        fun startActivity(context: Context, user: User, password: String){
            val intent = Intent(context, CountrySelectSignUpActivity::class.java)
            intent.putExtra(COUNTRY_USER_EXTRA, user)
            intent.putExtra(COUNTRY_PASSWORD_EXTRA, password)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}