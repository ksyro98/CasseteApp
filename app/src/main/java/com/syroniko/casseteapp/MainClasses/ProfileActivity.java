package com.syroniko.casseteapp.MainClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.syroniko.casseteapp.R;

import java.util.ArrayList;
import java.util.List;


public  class ProfileActivity extends AppCompatActivity {

    private final String TAG = ProfileActivity.class.getSimpleName();

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
    private User user;
    private String anotherUserId = "aT2i8Gq5yiY0BHTK0tTtYNBPKXC3";


    TextView nameTextView;
    TextView ageAndCountryTextView;
    TextView descriptionTextView;
    TextView friendsTextView;
    TextView songsReceivedTextView;
    TextView songsSentNumberTextView;
    TextView songsAcceptedNumberTextView;
    ImageView userProfileImageView;
    TextView aboutUserTextView;
    TextView friendsNumberTextView;
    TextView addFriendTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView=findViewById(R.id.activity_profile_name);
        //   ageAndCountryTextView=findViewById(R.id.activity_profile_age_and_country);
//            friendsTextView=findViewById(R.id.activity_profile_friends);
       //     songsReceivedTextView=findViewById(R.id.qctivity_profile_songs_received);
        songsSentNumberTextView = findViewById(R.id.activity_profile_songs_sent_number);
        songsAcceptedNumberTextView = findViewById(R.id.activity_profile_songs_received_number);
        userProfileImageView=findViewById(R.id.activity_profile_image);
        aboutUserTextView = findViewById(R.id.about_user_tv);
        friendsNumberTextView = findViewById(R.id.activity_profile_friends_number);
        addFriendTextView = findViewById(R.id.add_friend_tv);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.boxicon);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);
        userProfileImageView.setImageDrawable(roundedBitmapDrawable);


        final String uid = FirebaseAuth.getInstance().getUid();
        if(uid == null){
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(anotherUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("name");
                        nameTextView.setText(name);
                        String aboutUser = "About " + name;
                        aboutUserTextView.setText(aboutUser);

                        try {
                            friendList = (ArrayList<String>) documentSnapshot.get("friends");
                            friendsNumberTextView.setText(Integer.toString(friendList.size()));
                        }
                        catch (ClassCastException e){
                            Log.e(TAG, e.toString());
                        }
                        catch (NullPointerException e){
                            Log.e(TAG, e.toString());
                            friendsNumberTextView.setText("0");
                        }


//                        Log.d(TAG, documentSnapshot.get("songsSent").toString());

                        songsSentNumber = documentSnapshot.getLong("songsSent").intValue();
                        songsAcceptedNumber = documentSnapshot.getLong("songsAccepted").intValue();
                        songsSentNumberTextView.setText(Integer.toString(songsSentNumber));
                        songsAcceptedNumberTextView.setText(Integer.toString(songsAcceptedNumber));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "There was an error while retrieving your data.", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                        if(friends.contains(anotherUserId)){
                            addFriendTextView.setText("Already Friends");
                            addFriendTextView.setClickable(false);
                        }
                        else{
                            addFriendTextView.setText("Add Friends");
                            addFriendTextView.setClickable(true);
                        }
                    }
                });

        addFriendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users")
                        .document(uid)
                        .update(
                                "friends",
                                FieldValue.arrayUnion(anotherUserId)
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addFriendTextView.setText("Already Friends");
                                addFriendTextView.setClickable(false);
                                Toast.makeText(ProfileActivity.this, "Friend added!", Toast.LENGTH_SHORT).show();

                                friendList.add(uid);
                                friendsNumberTextView.setText(Integer.toString(friendList.size()));

                                db.collection("users")
                                        .document(anotherUserId)
                                        .update(
                                                "friends",
                                                FieldValue.arrayUnion(uid)
                                        );

                            }
                        });
            }
        });

        }
    }
