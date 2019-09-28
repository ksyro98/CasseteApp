package com.syroniko.casseteapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcoming);



        TextView tx = (TextView)findViewById(R.id.app_quote_textview);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");

        tx.setTypeface(custom_font);

        TextView loginButton=findViewById(R.id.login_but_welc_act);
        TextView signupButton=findViewById(R.id.signup_but_welc_act);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(WelcomingActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(WelcomingActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
    }
}
