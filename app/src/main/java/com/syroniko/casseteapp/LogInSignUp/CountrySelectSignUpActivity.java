package com.syroniko.casseteapp.LogInSignUp;

import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.MainClasses.User;
import com.syroniko.casseteapp.firebase.Auth;
import com.syroniko.casseteapp.firebase.AuthCallback;
import com.syroniko.casseteapp.firebase.UserDB;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import org.jetbrains.annotations.Nullable;

public class CountrySelectSignUpActivity extends AppCompatActivity {
    public static final String COUNTRY_USER_EXTRA = "genres user extra";
    public static final String COUNTRY_PASSWORD_EXTRA = "genres password extra";

    private static final String TAG = CountrySelectSignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_select_sign_up);

        TextView locationTextView = findViewById(R.id.location_text_view);
        TextView finishTextView = findViewById(R.id.finsihTextView);
        final TextView countryTextView = findViewById(R.id.countryTextView);

        TextView tx = findViewById(R.id.pick_country_string);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        tx.setTypeface(custom_font);

        Intent intent = getIntent();
        final User user = intent.getParcelableExtra(COUNTRY_USER_EXTRA);
        final String password = intent.getStringExtra(COUNTRY_PASSWORD_EXTRA);


        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        countryTextView.setText(name);
                        user.setCountry(name);
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        finishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getCountry() != null) {
//                    FirebaseAuth auth = FirebaseAuth.getInstance();
//
//                    auth.createUserWithEmailAndPassword(user.getEmail(), password)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    Toast.makeText(CountrySelectSignUpActivity.this, "Sign Up Comleted", Toast.LENGTH_SHORT).show();
//
//                                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//                                    String uid = FirebaseAuth.getInstance().getUid();
//                                    if(uid != null) {
//                                        user.setUid(uid);
//                                        firebaseFirestore.collection("users").document(uid).set(user);
//                                    }
//
//                                    //TODO add a cover here
//
//                                    Intent resultIntent = new Intent();
//                                    setResult(RESULT_OK, resultIntent);
//                                    finish();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(CountrySelectSignUpActivity.this, "Sign Up Failed. Please try again.", Toast.LENGTH_SHORT).show();
//                                    Log.e(TAG, e.toString());
//                                }
//                            });

                    Auth.INSTANCE.signUpWithEmail(
                            user.getEmail(),
                            password,
                            new AuthCallback() {
                                @Override
                                public void onSuccess(@Nullable String uid) {
                                    Toast.makeText(CountrySelectSignUpActivity.this, "Sign Up Completed", Toast.LENGTH_SHORT).show();
                                    if(uid != null) {
                                        user.setUid(uid);
                                        UserDB.INSTANCE.addUser(uid, user);

                                        Intent resultIntent = new Intent();
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(CountrySelectSignUpActivity.this, "Sign Up Failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
                else{
                    Toast.makeText(CountrySelectSignUpActivity.this, "Please select a country.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
