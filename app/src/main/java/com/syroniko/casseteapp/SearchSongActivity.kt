package com.syroniko.casseteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.syroniko.casseteapp.mainClasses.USER_MAIN_EXTRA
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.trackSearchFlow.SpotifyResultActivity

class SearchSongActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_song)

        val user = intent.getParcelableExtra<User>(USER_MAIN_EXTRA)


        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val resultsButton = findViewById<TextView>(R.id.resultsButton)

        resultsButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            SpotifyResultActivity.startActivity(this, searchQuery, user)
        }
    }
}