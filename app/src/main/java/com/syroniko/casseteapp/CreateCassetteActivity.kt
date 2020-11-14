package com.syroniko.casseteapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.syroniko.casseteapp.MainClasses.TOKEN_MAIN_EXTRA
import com.syroniko.casseteapp.R

class CreateCassetteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cassette)

        var token =intent.getStringExtra(TOKEN_MAIN_EXTRA)
        Log.v("zapowa",token!!)

        val searchSongButton: View =findViewById(R.id.pick_your_song_button_create_cassette)
        searchSongButton.setOnClickListener{
            val i = Intent(this, SearchSongActivity::class.java)
            i.putExtra(TOKEN_MAIN_EXTRA, token)
            startActivity(i)
        }
        val tx = findViewById<TextView>(R.id.create_cassette_headline)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont
    }

    companion object {
        fun startActivity(context: Context){
            val i = Intent(context, CreateCassetteActivity::class.java)
            context.startActivity(i)
        }
    }
}