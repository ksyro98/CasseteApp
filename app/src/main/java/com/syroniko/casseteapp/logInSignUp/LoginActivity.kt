package com.syroniko.casseteapp.logInSignUp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.AuthCallback
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.utils.*
import kotlinx.android.synthetic.main.activity_login.*

const val RC_SIGN_IN = 543

class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var user: User

    //add auth functionality to a view model
    private val auth = Auth(object : AuthCallback{
        override fun onSuccess(uid: String?) {
            loginToApp(uid)
        }

        override fun onFailure() {
            toast("Authentication failed.")
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
       
        val tx = findViewById<TextView>(R.id.login_to_account_string)
        val customFont = Typeface.createFromAsset(assets, "font/opensansregular.tff")
        tx.typeface = customFont

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        loginTextViewButton.setOnClickListener {
            if (loginEmail.text.toString() == "" || loginPassword.text.toString() == "") {
                toast("Please fill your account details.")
                return@setOnClickListener
            }

            auth.signInWithEmail(loginEmail.text.toString(), loginPassword.text.toString())
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        forgotPasswordTextView.setOnClickListener {
            ResetPasswordActivity.startActivity(this)
        }

        loginEmail.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            val colorStateList = ColorStateList.valueOf(Color.parseColor("#0fa59b"))
            val colorStateList2 = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            ViewCompat.setBackgroundTintList(loginEmail, colorStateList)
            ViewCompat.setBackgroundTintList(loginPassword, colorStateList2)
        }

        loginPassword.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            val colorStateList = ColorStateList.valueOf(Color.parseColor("#0fa59b"))
            val colorStateList2 = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            ViewCompat.setBackgroundTintList(loginPassword, colorStateList)
            ViewCompat.setBackgroundTintList(loginEmail, colorStateList2)
        }

    }

    private fun loginToApp(uid: String?) {
        if (uid == null) {
            toast("There was a problem retrieving your data. Please try again.")
            return
        }

        UserDB.getDocumentFromId(uid)
            .addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject(User::class.java) ?: return@addOnSuccessListener
                user.uid = uid
                if (user.spotifyToken == SPOTIFY_NO_TOKEN) {
                    val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    val request = builder.build()
                    AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request)
                }
                else {
                    MainActivity.startActivity(this, uid, user)
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
                else {
                    toast("Please make sure that you have an active account.")
                }
            }
            catch (e: ApiException) {
                Log.e("TAG", "Google sign in failed", e)
            }
        }
        else if(requestCode == SPOTIFY_REQUEST_CODE){
            val response = AuthenticationClient.getResponse(resultCode, data)
            onSpotifyResponse(response) { token ->
                user.spotifyToken = token
                if (user.uid != null) {
                    UserDB.update(user.uid!!, hashMapOf(Pair("spotifyToken", token)))
                }
                MainActivity.startActivity(this, user.uid, user)
                finish()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        auth.signInWithGoogle(account)
    }


    companion object{
        fun startActivity(context: Context){
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

}