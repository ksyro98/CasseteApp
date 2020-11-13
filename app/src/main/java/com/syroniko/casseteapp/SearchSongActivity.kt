package com.syroniko.casseteapp

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.syroniko.casseteapp.MainClasses.MainViewModel
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.SpotifyResultActivity

class SearchSongActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_song)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val resultsButton = findViewById<TextView>(R.id.resultsButton)

        resultsButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            SpotifyResultActivity.startActivity(this, searchQuery, viewModel.token)
        }
    }
}