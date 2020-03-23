package com.syroniko.casseteapp.LogInSignUp;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.MainClasses.User;

import java.util.ArrayList;
import java.util.List;

public class PickGenresSignUpActivity extends AppCompatActivity {
    private List<GenreNameImageForSignupAdapter> list = new ArrayList<>();
    private ArrayList<String> genreList=new ArrayList<>();
    private GenrePickSignupAdapter adapter;
    private RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_genres_sign_up);
        Intent intent =getIntent();
        final User user=intent.getParcelableExtra("User");
        final String password=intent.getStringExtra("Password");

        list.add(new GenreNameImageForSignupAdapter("Blues",R.drawable.blues,R.drawable.bluesgreen));
        list.add(new GenreNameImageForSignupAdapter("Classical",R.drawable.classicalgray,R.drawable.classicalgreen));
        list.add(new GenreNameImageForSignupAdapter("Country",R.drawable.countrygray,R.drawable.countrygreen));
        list.add(new GenreNameImageForSignupAdapter("Electronic",R.drawable.electronic_gray,R.drawable.electronic_green));
        list.add(new GenreNameImageForSignupAdapter("Folk",R.drawable.folkgray,R.drawable.folkgreen));
        list.add(new GenreNameImageForSignupAdapter("Hip-Hop",R.drawable.hip_hop_gray,R.drawable.hip_hop_green));
        list.add(new GenreNameImageForSignupAdapter("Jazz",R.drawable.jazz_gray,R.drawable.jazz_green));
        list.add(new GenreNameImageForSignupAdapter("Metal",R.drawable.metalgray,R.drawable.metal));
        list.add(new GenreNameImageForSignupAdapter("Pop",R.drawable.pop_mic_gray,R.drawable.pop_mic_green));
        list.add(new GenreNameImageForSignupAdapter("Punk",R.drawable.punkgray,R.drawable.punk_green));
        list.add(new GenreNameImageForSignupAdapter("R&B",R.drawable.rnbgray,R.drawable.rnbgreen));
        list.add(new GenreNameImageForSignupAdapter("Rock",R.drawable.rockgray,R.drawable.rockgreen));
        list.add(new GenreNameImageForSignupAdapter("Soundtracks",R.drawable.tvigray,R.drawable.tv_green));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_signup_genres);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter=new GenrePickSignupAdapter(this, list, new GenrePickSignupAdapter.GenreListClickListener() {
            @Override
            public void OnListItemClick(int clickedItemPosition) {
                if(!list.get(clickedItemPosition).getClicked()){
                    genreList.add(list.get(clickedItemPosition).getGenre());
                }
                else{
                    for(int i=0; i<genreList.size(); i++){
                        if(list.get(clickedItemPosition).getGenre().equals(genreList.get(i))){
                            genreList.remove(i);
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        findViewById(R.id.recycler_signup_genres).setFocusable(false);
        TextView tx = (TextView)findViewById(R.id.pickthegenresyouenjou_string);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");

        tx.setTypeface(custom_font);

        TextView next = findViewById(R.id.nextfromgenres_to);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genreList.size() == 0)
                    Toast.makeText(PickGenresSignUpActivity.this, "Please pick a genre", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(PickGenresSignUpActivity.this, CountrySelectActivitySignUp.class);
                    user.setGenres(genreList);
                    intent.putExtra("User", user);
                    intent.putExtra("Password", password);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
