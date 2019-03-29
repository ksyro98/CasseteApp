package com.syroniko.casseteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_spotify_result.*

class SpotifyResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_result)

//        val intent = intent
        if(intent == null){
            return
        }


        val resultList = intent.extras[spotifyResultExtraName] as ArrayList<SpotifyResult>
        val token = intent.getStringExtra(tokenExtraName)

        spotifyResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        spotifyResultsRecyclerView.setHasFixedSize(true)


        val spotifyAdapter = SpotifyAdapter(this, resultList, token)

        spotifyResultsRecyclerView.adapter = spotifyAdapter
    }
}
