package com.syroniko.casseteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_spotify_artist_result.*
import kotlinx.android.synthetic.main.activity_spotify_result.*
import org.json.JSONObject

class SpotifyArtistResultActivity : AppCompatActivity() {

    private val artistResultList = arrayListOf<SpotifyResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_artist_result)

        if(intent == null){
            return;
        }

        val artistId = intent.getStringExtra(spotifyArtistResultExtraName)
        val token = intent.getStringExtra(tokenExtraName)

        val query = "https://api.spotify.com/v1/search?q=*&artist=$artistId&type=track"

        val context = this

        val queue = Volley.newRequestQueue(this)

        val searchRequest = SearchRequest(
            Request.Method.GET,
            query,
            token,
            Response.Listener<JSONObject> { response ->

                Log.d("ArtistResult", response.toString())
                toast(response.toString())

            },
            Response.ErrorListener { error -> toast("Error. :("); Log.d("ArtistResult", error.toString()); toast(error.toString())})

        queue.add(searchRequest)


        spotifyArtistResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        spotifyArtistResultsRecyclerView.setHasFixedSize(true)


        val spotifyAdapter = SpotifyAdapter(this, artistResultList, token)

        spotifyArtistResultsRecyclerView.adapter = spotifyAdapter




    }

}
