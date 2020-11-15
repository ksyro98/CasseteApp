package com.syroniko.casseteapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.syroniko.casseteapp.mainClasses.USER_MAIN_EXTRA
import com.syroniko.casseteapp.mainClasses.User

class CreateCassetteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cassette)

        val user = intent.getParcelableExtra<User>(USER_MAIN_EXTRA)

        val searchSongButton: View =findViewById(R.id.pick_your_song_button_create_cassette)
        searchSongButton.setOnClickListener{
            val i = Intent(this, SearchSongActivity::class.java)
            i.putExtra(USER_MAIN_EXTRA, user)
            startActivity(i)
        }
        val tx = findViewById<TextView>(R.id.create_cassette_headline)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont
    }

    companion object {
        fun startActivity(context: Context, user: User){
            val i = Intent(context, CreateCassetteActivity::class.java)
            i.putExtra(USER_MAIN_EXTRA, user)
            context.startActivity(i)
        }
    }
}