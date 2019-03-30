package com.syroniko.casseteapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class CountrySelectActivitySignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_select_sign_up);

        Intent intent =getIntent();
        final User user=intent.getParcelableExtra("User");
        final String password=intent.getStringExtra("Password");
        final ArrayList<String>  genres=intent.getStringArrayListExtra("Genres");


        TextView tx=findViewById(R.id.pick_country_string);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");

        tx.setTypeface(custom_font);

    }
}
