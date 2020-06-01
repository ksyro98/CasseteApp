

package com.syroniko.casseteapp.LogInSignUp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.syroniko.casseteapp.MainClasses.MainActivity;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.firebase.Auth;
import com.syroniko.casseteapp.firebase.AuthCallback;

import org.jetbrains.annotations.Nullable;

import static com.syroniko.casseteapp.MainClasses.MainActivityKt.UID_MAIN_EXTRA;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =543 ;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView tx = findViewById(R.id.login_to_account_string);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        tx.setTypeface(custom_font);

        final EditText loginEmail=findViewById(R.id.login_email);
        final EditText loginPass=findViewById(R.id.login_password);
        TextView forgotPasswordTextView = findViewById(R.id.forgot_password_textview);
        TextView loginTV = findViewById(R.id.login_textview_button);
        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginEmail.getText().toString().equals("") || loginPass.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill your account details.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Auth.INSTANCE.signInWithEmail(
                        loginEmail.getText().toString(),
                        loginPass.getText().toString(),
                        new AuthCallback() {
                            @Override
                            public void onSuccess(String uid) {
                                LoginToApp(uid);
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(i);
            }
        });

        loginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#0fa59b"));
                ColorStateList colorStateList2 = ColorStateList.valueOf(Color.parseColor("#9E9E9E"));
                ViewCompat.setBackgroundTintList(loginEmail,colorStateList);
                ViewCompat.setBackgroundTintList(loginPass,colorStateList2);
            }
        });

        loginPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#0fa59b"));
                ColorStateList colorStateList2 = ColorStateList.valueOf(Color.parseColor("#9E9E9E"));
                ViewCompat.setBackgroundTintList(loginPass,colorStateList);
                ViewCompat.setBackgroundTintList(loginEmail,colorStateList2);

            }
        });

    }

    private void LoginToApp(String uid) {
        Intent i=new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra(UID_MAIN_EXTRA, uid);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
                else{
                    Toast.makeText(this, "Please make sure that you have an active account.", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Auth.INSTANCE.signInWithGoogle(
                account,
                new AuthCallback() {
                    @Override
                    public void onSuccess(@Nullable String uid) {
                        LoginToApp(uid);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}
