package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.syroniko.casseteapp.MainClasses.spotifyQueryExtraName
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.MainClasses.tokenExtraName
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifySeparator
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import kotlinx.android.synthetic.main.activity_spotify_result.*
import org.json.JSONObject


const val noPreviewUrl = ""

class SpotifyResultActivity : AppCompatActivity() {

    private val resultsList = arrayListOf<SpotifyResult>()
    private val tracks = arrayListOf<SpotifyResult>()
    private val artists = arrayListOf<SpotifyResult>()
    private var searchDone = false
    private var spotifyAdapter = SpotifyAdapter(this, resultsList, null)

    private val tag = SpotifyResultActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_result)

        if(intent == null){
            return
        }

        val searchQuery = intent.getStringExtra(spotifyQueryExtraName)
        val token = intent.getStringExtra(tokenExtraName)
        val queue = Volley.newRequestQueue(this)


        spotifyResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        spotifyResultsRecyclerView.setHasFixedSize(true)
        spotifyAdapter.setToken(token)

        spotifyResultsRecyclerView.adapter = spotifyAdapter

        searchTrack(prepareTrackQuery(searchQuery), this, queue, token, ::updateData)
        searchArtist(prepareArtistQuery(searchQuery), this, queue, token, ::updateData)
    }




    private fun prepareTrackQuery(searchQuery: String): String {
        val searchKeyWord = searchQuery.replace(" ", "%20")

        return if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
            "https://api.spotify.com/v1/search?q=$searchKeyWord&type=track"
        } else {
            "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=track"
        }
    }

    private fun prepareArtistQuery(searchQuery: String): String{
        val searchKeyWord = searchQuery.replace(" ", "%20")

        return if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
            "https://api.spotify.com/v1/search?q=$searchKeyWord&type=artist"
        } else {
            "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=artist"
        }
    }

    private fun searchTrack(searchQueryUrl: String, context: Context, queue: RequestQueue, token: String, action: () -> Unit){
        val searchRequest = SearchRequest(
            Request.Method.GET,
            searchQueryUrl,
            token,
            Response.Listener<JSONObject> { response ->

                val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
                val items = (jsonObject.get("tracks") as JsonObject).get("items") as JsonArray

                for (track in items) {
                    val jsonTrack = track as JsonObject
                    val trackName = jsonTrack.get("name").asString

                    val trackId = jsonTrack.get("id").asString

                    val artistIds = arrayListOf<String>()
                    val artistNames = arrayListOf<String>()
                    for (artist in jsonTrack.getAsJsonArray("artists")) {
                        artistIds.add((artist as JsonObject).get("id").asString)
                        artistNames.add((artist as JsonObject).get("name").asString)
                    }

                    val imageUrl =
                        (jsonTrack.getAsJsonObject("album").getAsJsonArray("images")[0] as JsonObject).get("url")
                            .asString

                    Log.d(tag, jsonTrack.toString())

                    //jsonTrack.get("preview_url").asString != null) doesn't work
                    val previewUrl: String? = if (jsonTrack.get("preview_url").toString() != "null") {
                        jsonTrack.get("preview_url").asString
                    }
                    else{
                        noPreviewUrl
                    }


                    val spotifyTrack =
                        SpotifyTrack(
                            trackName,
                            trackId,
                            artistIds,
                            artistNames,
                            imageUrl,
                            null,
                            previewUrl
                        )
                    tracks.add(spotifyTrack)
//                    resultsList.add(spotifyTrack)
                }

                Log.d(tag, "track result!!!")
                Log.d(tag, searchDone.toString())

                if(searchDone){
                    addToResultsList()
                }
                else {
                    searchDone = true
                }

                action()

            },
            Response.ErrorListener { error ->
                Log.e(tag, "Volley Error", error); context.toast("Error. :("); Log.d(
                tag,
                error.toString()
            )
            })

        queue.add(searchRequest)
    }

    private fun searchArtist(searchQueryUrl: String, context: Context, queue: RequestQueue, token: String, action: () -> Unit){
        val searchRequest = SearchRequest(
            Request.Method.GET,
            searchQueryUrl,
            token,
            Response.Listener<JSONObject> { response ->

                val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
                val items = (jsonObject.get("artists") as JsonObject).get("items") as JsonArray


                for (artist in items) {
                    val jsonArtist = artist as JsonObject

                    val artistName = jsonArtist.get("name").asString

                    val artistId = jsonArtist.get("id").asString

                    val imageUrl = if (jsonArtist.getAsJsonArray("images").size() > 0) {
                        (jsonArtist.getAsJsonArray("images")[0] as JsonObject).get("url").asString
                    } else {
                        ""
                    }

                    val spotifyArtist = SpotifyArtist(artistName, artistId, imageUrl)
                    artists.add(spotifyArtist)
//                    resultsList.add(spotifyArtist)
                }

//                resultsList.addAll(artists)

                Log.d(tag, "artists results!!!")
                Log.d(tag, searchDone.toString())

                if(searchDone) {
                    addToResultsList()
                }
                else{
                    searchDone = true
                }

                action()
            },
            Response.ErrorListener { error ->
                Log.e(tag, "Volley Error", error); context.toast("Error. :("); Log.d(
                tag,
                error.toString()
            )
            })

        queue.add(searchRequest)
    }

    private fun addToResultsList(){
        if(tracks.size != 0) {
            resultsList.add(SpotifySeparator("Tracks"))
        }
        resultsList.addAll(tracks)
        if (artists.size != 0) {
            resultsList.add(SpotifySeparator("Artists"))
        }
        resultsList.addAll(artists)
    }

    private fun updateData(){
        spotifyAdapter.notifyDataSetChanged()
    }

}
