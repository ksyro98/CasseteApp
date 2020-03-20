package com.syroniko.casseteapp.MainClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.syroniko.casseteapp.CaseFragment;
import com.syroniko.casseteapp.CreateCassetteFragment;
import com.syroniko.casseteapp.ChatAndMessages.MessagesFragment;
import com.syroniko.casseteapp.R;

import java.util.HashMap;
import java.util.Map;

public class CoreActivity extends AppCompatActivity {
    public static String uid;
   FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        getSupportFragmentManager().beginTransaction().replace(R.id.core_fragment_container,new CaseFragment()).commit();

        Intent i=getIntent();
        uid=i.getStringExtra("USERID");

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch (item.getItemId()){
                    case R.id.bot_nav_cassette_case:
                        selectedFragment=new CaseFragment();
                        break;
                    case R.id.bot_nav_messages:
                        selectedFragment=new MessagesFragment();
                        break;
                    case R.id.bot_nav_new_cassette:
                        selectedFragment=new CreateCassetteFragment();
                        break;


                }
                getSupportFragmentManager().beginTransaction().replace(R.id.core_fragment_container,selectedFragment).commit();
                return true;
            }
        });


    }



    public String getUid(){
        return uid;
    }

}
