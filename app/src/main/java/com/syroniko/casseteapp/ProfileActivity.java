package com.syroniko.casseteapp;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private String name;
    private List<String> genreList=new ArrayList<>();
    private List<String> friendList=new ArrayList<>();
    private int age;
    private List<String> favouriteBands=new ArrayList<>();
    private String favouriteSongUrlForYoutubeWindowRedirect;
    private int songsSentNumber;
    private int songsAcceptedNumber;
  //  private List<Bitmap> images=new ArrayList<>();
    private Bitmap userProfileImage;
    private String description;
    private String country;
    User user;


    TextView nameTextView;
    TextView ageAndCountryTextView;
    TextView descriptionTextView;
    TextView friendsTextView;
    TextView songsReceivedTextView;
    TextView songsSentTextView;
    ImageView userProfileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView=findViewById(R.id.activity_profile_name);
        ageAndCountryTextView=findViewById(R.id.activity_profile_age_and_country);
        descriptionTextView=findViewById(R.id.activity_profile_description);
        friendsTextView=findViewById(R.id.activity_profile_friends);
        songsReceivedTextView=findViewById(R.id.qctivity_profile_songs_received);
        songsSentTextView=findViewById(R.id.activity_profile_songs_sent);
        userProfileImageView=findViewById(R.id.activity_profile_image);
    }
}
