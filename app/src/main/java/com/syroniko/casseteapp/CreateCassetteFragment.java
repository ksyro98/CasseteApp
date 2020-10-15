package com.syroniko.casseteapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.syroniko.casseteapp.TrackSearchFlow.SpotifyResultActivity;

public class CreateCassetteFragment extends Fragment {
    private String token = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_cassette,container,false);
        final Context context = getContext();

        final EditText searchEditText = view.findViewById(R.id.searchEditText);
        Button resultsButton = view.findViewById(R.id.resultsButton);

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = searchEditText.getText().toString();
                if(context != null) {
                    SpotifyResultActivity.Companion.startActivity(context, searchQuery, token);
                }
            }
        });

        return view;
    }

    public void setToken(String token){
        this.token = token;
    }
}
