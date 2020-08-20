package com.syroniko.casseteapp.LogInSignUp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.MainClasses.UID_MAIN_EXTRA
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.AuthCallback
import kotlinx.android.synthetic.main.activity_login.*

const val RC_SIGN_IN = 543

class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient

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
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
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
        MainActivity.startActivity(this, uid)
        finish()
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