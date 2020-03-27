

package com.syroniko.casseteapp.LogInSignUp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.syroniko.casseteapp.MainClasses.CoreActivity;
import com.syroniko.casseteapp.MainClasses.MainActivity;
import com.syroniko.casseteapp.R;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =543 ;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    TextView loginTV;
    TextView userSignUpTextView;
    TextView forgotPasswordTextView;
    EditText loginPass;
    EditText loginEmail;
    public static final String MyPREFERENCES = "MyAuthPreferences" ;
    public static final String Email = "emailKey";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        loginEmail=findViewById(R.id.login_email);
        loginPass=findViewById(R.id.login_password);

        TextView tx = (TextView)findViewById(R.id.login_to_account_string);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        tx.setTypeface(custom_font);

        forgotPasswordTextView=findViewById(R.id.forgot_password_textview);
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

        //android:id="@+id/create_user_account_textview"////////////////////////////////////////////////////////////////////////////////


        loginTV=findViewById(R.id.login_textview_button);
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginEmail.getText().toString().equals("")|| loginPass.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill your account details", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                                    LoginToAppIntent();


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


//        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void LoginToAppIntent() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Email, mAuth.getCurrentUser().getEmail());

        editor.apply();
        Intent i=new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("USERID",mAuth.getCurrentUser().getUid());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
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
                firebaseAuthWithGoogle(account);                                //TO DO :WARNING CAN BE NULL(ACCOUNT)
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginToAppIntent();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
