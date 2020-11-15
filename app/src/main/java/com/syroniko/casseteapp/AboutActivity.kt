package com.syroniko.casseteapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*

const val UID_NAME = "uid name"
const val USER_NAME = "user name"
const val COUNTRY_NAME = "country name"
const val BIO_NAME = "bio name"
const val GENRES_NAME = "genres name"
const val FAVORITE_ARTISTS_NAME = "favorite artists name"
const val INTERESTS_NAME = "interests name"

class AboutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val uid = intent.getStringExtra(UID_NAME)
        val name = intent.getStringExtra(USER_NAME)
        val country = intent.getStringExtra(COUNTRY_NAME)
        val bio = intent.getStringExtra(BIO_NAME)
        val genres = intent.getStringArrayListExtra(GENRES_NAME)
        val favoriteArtists = intent.getStringArrayListExtra(FAVORITE_ARTISTS_NAME)
        val interests = intent.getStringArrayListExtra(INTERESTS_NAME)

        aboutTextView.text = "About $name"

        countryTextView.text = "Country:\n"
        countryTextView.append(country)

        bioTextView.text = "Bio:\n"
        bioTextView.append(bio)

        genresTextView.text = "Genres:\n"
        fillTextViewWithList(genresTextView, genres)

        artistsTextView.text = "Favourite Artists:\n"
        fillTextViewWithList(artistsTextView, favoriteArtists)

        interestsTextView.text = "Interests:\n"
        fillTextViewWithList(interestsTextView, interests)

    }

    private fun fillTextViewWithList(textView: TextView, arrayList: ArrayList<String>?){
        if(arrayList == null){
            return
        }
        for(item in arrayList){
            textView.append(item)
            if(item != arrayList.last()) {
                textView.append("\n")
            }
        }
    }
}
