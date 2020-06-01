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

import static com.syroniko.casseteapp.LogInSignUp.CountrySelectSignUpActivity.COUNTRY_PASSWORD_EXTRA;
import static com.syroniko.casseteapp.LogInSignUp.CountrySelectSignUpActivity.COUNTRY_USER_EXTRA;

public class PickGenresSignUpActivity extends AppCompatActivity {
    public static final String GENRES_USER_EXTRA = "genres user extra";
    public static final String GENRES_PASSWORD_EXTRA = "genres password extra";

    private List<GenreNameImageForSignupAdapter> list = GenresListKt.getList();
    private ArrayList<String> genreList=new ArrayList<>();
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_genres_sign_up);

        RecyclerView recyclerView = findViewById(R.id.recycler_signup_genres);
        TextView next = findViewById(R.id.nextfromgenres_to);

        TextView tx = findViewById(R.id.pickthegenresyouenjou_string);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        tx.setTypeface(custom_font);

        Intent intent = getIntent();
        final User user=intent.getParcelableExtra(GENRES_USER_EXTRA);
        final String password=intent.getStringExtra(GENRES_PASSWORD_EXTRA);


        gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);

        GenrePickSignupAdapter adapter = new GenrePickSignupAdapter(list, new GenrePickSignupAdapter.GenreListClickListener() {
            @Override
            public void OnListItemClick(int clickedItemPosition) {
                if (!list.get(clickedItemPosition).isClicked()) {
                    genreList.add(list.get(clickedItemPosition).getGenre());
                }
                else {
                    genreList.remove(list.get(clickedItemPosition).getGenre());
                }
            }
        });

        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setFocusable(false);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genreList.size() == 0)
                    Toast.makeText(PickGenresSignUpActivity.this, "Please pick a genre.", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(PickGenresSignUpActivity.this, CountrySelectSignUpActivity.class);
                    user.setGenres(genreList);
                    intent.putExtra(COUNTRY_USER_EXTRA, user);
                    intent.putExtra(COUNTRY_PASSWORD_EXTRA, password);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
