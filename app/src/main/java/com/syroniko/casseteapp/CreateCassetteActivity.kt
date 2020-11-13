package com.syroniko.casseteapp

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.syroniko.casseteapp.R

class CreateCassetteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cassette)

        val searchSongButton: View =findViewById(R.id.pick_your_song_button_create_cassette)
        searchSongButton.setOnClickListener{
            val i = Intent(this, SearchSongActivity::class.java)
            startActivity(i)
        }
        val tx = findViewById<TextView>(R.id.create_cassette_headline)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont
    }
}