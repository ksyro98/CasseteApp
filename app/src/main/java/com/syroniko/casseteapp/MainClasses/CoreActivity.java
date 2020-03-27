package com.syroniko.casseteapp.MainClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.syroniko.casseteapp.CassetteAdapter;
import com.syroniko.casseteapp.CassetteCaseFragment;
import com.syroniko.casseteapp.CreateCassetteFragment;
import com.syroniko.casseteapp.ChatAndMessages.MessagesFragment;
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist;
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult;
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack;
import com.syroniko.casseteapp.room.LocalCassette;

import java.util.ArrayList;

import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.*;
import static com.syroniko.casseteapp.MainClasses.MainActivityKt.clientId;
import static com.syroniko.casseteapp.MainClasses.MainActivityKt.longToast;
import static com.syroniko.casseteapp.MainClasses.MainActivityKt.redirectUri;
import static com.syroniko.casseteapp.MainClasses.MainActivityKt.spotifyRequestCode;
import static com.syroniko.casseteapp.MainClasses.MainActivityKt.thirtyMins;

public class CoreActivity extends AppCompatActivity {
//    private final String TAG = CoreActivity.class.getSimpleName();
//
//    public static String uid = "";
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private String token = null;
//    private ArrayList<SpotifyTrack> tracks;
//    private ArrayList<SpotifyArtist> artists;
//    private ArrayList<SpotifyResult> resultsList;
//
//    private boolean searchDone = false;
//    private ArrayList<LocalCassette> cassettes = new ArrayList<>();
//    private CassetteAdapter cassetteAdapter = new CassetteAdapter(this, cassettes);
//    private CassetteCaseFragment cassetteCaseFragment;// = new CassetteCaseFragment();
//    private MessagesFragment messagesFragment;// = new MessagesFragment();
//    private CreateCassetteFragment createCassetteFragment;// = new CreateCassetteFragment();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_core);
//
//
//        cassetteCaseFragment = new CassetteCaseFragment();
//        messagesFragment = new MessagesFragment();
//        createCassetteFragment = new CreateCassetteFragment();
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.core_fragment_container, cassetteCaseFragment).commit();
//
//
////
////        if (canGetCassette){
////            cassetteCaseFragment.getCassette();
////        }
//
//        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment selectedFragment=null;
//                switch (item.getItemId()){
//                    case R.id.bot_nav_cassette_case:
//                        selectedFragment = cassetteCaseFragment;
//                        break;
//                    case R.id.bot_nav_messages:
//                        selectedFragment = messagesFragment;
//                        break;
//                    case R.id.bot_nav_new_cassette:
//                        selectedFragment = createCassetteFragment;
//                        ((CreateCassetteFragment) selectedFragment).setToken(token);
//                        break;
//                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.core_fragment_container,selectedFragment).commit();
//                return true;
//            }
//        });
//
//
//        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(clientId, TOKEN, redirectUri);
//        AuthenticationRequest request = builder.build();
//
//        AuthenticationClient.openLoginActivity(this, spotifyRequestCode, request);
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == spotifyRequestCode) {
//            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
//
//            switch (response.getType()) {
//                case TOKEN:
//                    Log.d(TAG, "token");
//                    token = response.getAccessToken();
//                    break;
//                case ERROR:
//                    Log.d(TAG, "error");
//                    Log.e(TAG, "spotify error" + response.getError());
//                    break;
//
//                case CODE:
//                    Log.d(TAG, "code");
//                    break;
//
//                case EMPTY:
//                    Log.d(TAG, "empty");
//                    break;
//
//                case UNKNOWN:
//                    Log.d(TAG, "unknown");
//                    break;
//
//                default:
//                    Log.d(TAG, "null");
//            }
//
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//        if (firebaseAuth.getUid() == null){
//            Intent intent = new Intent(this, WelcomingActivity.class);
//            startActivity(intent);
//        }
//        else{
//            uid = firebaseAuth.getUid();
//
//            db.collection("users")
//                    .document(uid)
//                    .get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            long time = (long) documentSnapshot.get("receivedLastCassetteAt");
//                            if(System.currentTimeMillis()-time > thirtyMins){
//                                cassetteCaseFragment.getCassette();
//                            }
//                        }
//                    });
//        }
//    }
//
//    public String getUid(){
//        return uid;
//    }

}
