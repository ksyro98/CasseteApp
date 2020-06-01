package com.syroniko.casseteapp.LogInSignUp;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.MainClasses.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.syroniko.casseteapp.LogInSignUp.PickGenresSignUpActivity.GENRES_PASSWORD_EXTRA;
import static com.syroniko.casseteapp.LogInSignUp.PickGenresSignUpActivity.GENRES_USER_EXTRA;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailET;
    private EditText password;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.name_et_sign_up);
        emailET = findViewById(R.id.email_et_signup1);
        password = findViewById(R.id.password_et_signup);
        password2 = findViewById(R.id.password_confirm_et_signup);
        TextView next = findViewById(R.id.nextfromsignup_togenres);

        TextView tx = findViewById(R.id.create_an_account_string);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        tx.setTypeface(custom_font);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pass = password.getText().toString();

                if(nameEditText.getText().toString().equals("") || emailET.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please fill all the info.", Toast.LENGTH_SHORT).show();
                }
                else if(!password.getText().toString().equals(password2.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                }
                else if (pass.length()<6){
                    Toast.makeText(SignUpActivity.this, "Password must have at least 6 characters.", Toast.LENGTH_SHORT).show();
                }
                else {
                    User userInstance = new User(nameEditText.getText().toString(),  emailET.getText().toString(),"offline", System.currentTimeMillis(), null, null, null, new ArrayList<String>(), new HashMap<String, Long>(), new ArrayList<String>(),0, 0, new ArrayList<String>(), "", new ArrayList<String>(), new ArrayList<String>());
                    Intent intent = new Intent(SignUpActivity.this, PickGenresSignUpActivity.class);
                    intent.putExtra(GENRES_USER_EXTRA, userInstance);
                    intent.putExtra(GENRES_PASSWORD_EXTRA, pass);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
