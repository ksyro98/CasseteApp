package com.syroniko.casseteapp;

import android.graphics.Bitmap;

public class GenreNameImageForSignupAdapter {
    private String genre;

    public Boolean getClicked() {
        return isClicked;
    }

    public void setClicked(Boolean clicked) {
        isClicked = clicked;
    }

    private Boolean isClicked=false;
    private int genreImageDefault;

    public int getGenreImageClicked() {
        return genreImageClicked;
    }

    public GenreNameImageForSignupAdapter(String genre,int defaultt, int clicked){
        genreImageDefault=defaultt;
        genreImageClicked=clicked;
        this.genre=genre;

    }
    public void setGenreImageClicked(int genreImageClicked) {
        this.genreImageClicked = genreImageClicked;
    }

    private int genreImageClicked;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getGenreImageDefault() {
        return genreImageDefault;
    }

    public void setGenreImageDefault(int genreImage) {
        this.genreImageDefault = genreImage;
    }
}
