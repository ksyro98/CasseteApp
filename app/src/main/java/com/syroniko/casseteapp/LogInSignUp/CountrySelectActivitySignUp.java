package com.syroniko.casseteapp.LogInSignUp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.MainClasses.User;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.util.ArrayList;

public class CountrySelectActivitySignUp extends AppCompatActivity {

    private static final String TAG = CountrySelectActivitySignUp.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_select_sign_up);

        Intent intent =getIntent();
        final User user = intent.getParcelableExtra("User");
        final String password = intent.getStringExtra("Password");
//        final ArrayList<String> genres = intent.getStringArrayListExtra("Genres");
//        user.setGenres(genres);


        TextView tx = findViewById(R.id.pick_country_string);
        TextView locationTextView = findViewById(R.id.location_text_view);
        TextView finishTextView = findViewById(R.id.finsihTextView);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        final TextView countryTextView = findViewById(R.id.countryTextView);


        tx.setTypeface(custom_font);


        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
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
                    FirebaseAuth auth = FirebaseAuth.getInstance();
//                    Log.d(TAG, user.getEmail());
                    auth.createUserWithEmailAndPassword(user.getEmail(), password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(CountrySelectActivitySignUp.this, "Sign Up Comleted", Toast.LENGTH_SHORT).show();

                                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                    String uid = FirebaseAuth.getInstance().getUid();
                                    if(uid != null) {
                                        user.setUid(uid);
                                        firebaseFirestore.collection("users").document(uid).set(user);
                                    }

                                    //TODO add a cover here

                                    Intent resultIntent = new Intent();
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CountrySelectActivitySignUp.this, "Sign Up Failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, e.toString());
                                }
                            });
                }
            }
        });
    }

}
