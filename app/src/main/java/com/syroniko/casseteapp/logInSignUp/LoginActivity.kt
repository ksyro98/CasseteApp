package com.syroniko.casseteapp.logInSignUp

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.AuthCallback
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

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
      //  tv.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular))



        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        loginTextViewButton.setOnClickListener {
            hideKeyboard(it)
            if (loginEmail.text.toString() == "" || loginPassword.text.toString() == "") {
                toast("Please fill your account details.")
                return@setOnClickListener
            }

            auth.signInWithEmail(loginEmail.text.toString(), loginPassword.text.toString())
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
            hideKeyboard(it)
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
                addFCMTokenWhenNeeded(user)
                MainActivity.startActivity(this, uid, user)
                finish()
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