package com.syroniko.casseteapp.LogInSignUp;

import com.syroniko.casseteapp.firebase.FirestoreDB;

public class GenreNameImageForSignupAdapter {
    private String genre;
    private Boolean clicked = false;
    private int genreImageDefault;
    private int genreImageClicked;

    Boolean isClicked() {
        return clicked;
    }

    void setClicked(Boolean clicked) {
        this.clicked = clicked;
    }

    int getGenreImageClicked() {
        return genreImageClicked;
    }

    String getGenre() {
        return genre;
    }

    int getGenreImageDefault() {
        return genreImageDefault;
    }

    public GenreNameImageForSignupAdapter(String genre, int defaultImage, int clicked){
        genreImageDefault = defaultImage;
        genreImageClicked = clicked;
        this.genre = genre;
    }
}
