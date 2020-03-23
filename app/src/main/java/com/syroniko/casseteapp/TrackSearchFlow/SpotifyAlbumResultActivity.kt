package com.syroniko.casseteapp.TrackSearchFlow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.MainClasses.tokenExtraName
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import kotlinx.android.synthetic.main.activity_spotify_album_result.*
import org.json.JSONObject

class SpotifyAlbumResultActivity : AppCompatActivity() {

    val trackList = arrayListOf<SpotifyResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_album_result)


        if(intent == null){
            return
        }

        val albumId = intent.getStringExtra(spotifyAlbumIdExtraName)
        val token = intent.getStringExtra(tokenExtraName)
        val albumImageUrl = intent.getStringExtra(spotifyAlbumImageUrlExtraName)

        val trackAdapter = SpotifyAdapter(this, trackList, token)

        val query = "https://api.spotify.com/v1/albums/$albumId/tracks"

        val queue = Volley.newRequestQueue(this)

        val searchRequest = SearchRequest(
            Request.Method.GET,
            query,
            token,
            Response.Listener<JSONObject> { response ->
                val items = response.getJSONArray("items")
                for (i in 0 until items.length()) {

                    val trackName = items.getJSONObject(i).getString("name")//.toString()
                    val trackId = items.getJSONObject(i).getString("id")

                    val artistsJsons = items.getJSONObject(i).getJSONArray("artists")
//                    Log.d("SpotifyAlbumResultAct", artistsJsons.toString())

                    val artistNames = arrayListOf<String>()
                    val artistIds = arrayListOf<String>()
                    for (j in 0 until artistsJsons.length()) {
                        artistNames.add(artistsJsons.getJSONObject(j).getString("name"))
                        artistIds.add(artistsJsons.getJSONObject(j).getString("id"))
                    }

                    val previewUrl: String? = if (items.getJSONObject(i).getString("preview_url") != null) {
                        items.getJSONObject(i).getString("preview_url")
                    }
                    else{
                        noPreviewUrl
                    }
//                    Log.d("SpotifyAlbumResultAct", artistNames.toString())
//                    Log.d("SpotifyAlbumResultAct", artistIds.toString())
//                    Log.d("SpotifyAlbumResultAct", "")

                    trackList.add(
                        SpotifyTrack(
                            trackName,
                            trackId,
                            artistIds,
                            artistNames,
                            albumImageUrl,
                            null,
                            previewUrl
                        )
                    )
                }

                trackAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                toast("Error. :("); Log.d(
                "AlbumResult",
                error.toString()
            ); toast(error.toString())
            }
        )

        queue.add(searchRequest)

        spotifyAlbumResultRecyclerView.layoutManager = LinearLayoutManager(this)
        spotifyAlbumResultRecyclerView.setHasFixedSize(true)
        spotifyAlbumResultRecyclerView.adapter = trackAdapter
    }
}
