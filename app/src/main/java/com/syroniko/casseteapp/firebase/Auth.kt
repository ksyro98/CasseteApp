package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase


class Auth(private val authCallback: AuthCallback = EmptyAuthCallback()){

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val tag = Auth::class.java.simpleName

    fun signInWithEmail(email: String, password: String, authCallback: AuthCallback = this.authCallback){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d(tag, "Log in with email was successful.")
                    authCallback.onSuccess(auth.uid)
                }
                else{
                    Log.e(tag, "Log in with email failed", task.exception)
                    authCallback.onFailure()
                }
            }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, authCallback: AuthCallback = this.authCallback){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Log.d(tag, "Log in with google was successful.")
                    authCallback.onSuccess(auth.uid)
                }
                else{
                    Log.e(tag, "Log in with google failed", task.exception)
                    authCallback.onFailure()
                }
            }
    }

    fun signUpWithEmail(email: String, password: String, authCallback: AuthCallback = this.authCallback){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(tag, "Sign up with email was successful.")
                authCallback.onSuccess(it.user?.uid)
            }
            .addOnFailureListener {
                Log.e(tag, "Sign up with email failed", it)
                authCallback.onFailure()
            }
    }

    fun resetPassword(email: String, authCallback: AuthCallback = this.authCallback){
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Log.d(tag, "Reset email sent.")
                authCallback.onSuccess(auth.uid)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Failed to send reset email.", e)
                authCallback.onFailure()
            }
    }


    class EmptyAuthCallback : AuthCallback{
        override fun onSuccess(uid: String?) {
            //do nothing
        }

        override fun onFailure() {
            //do nothing
        }
    }

    companion object {
        fun getUid(): String?{
            return FirebaseAuth.getInstance().uid
        }
    }
}