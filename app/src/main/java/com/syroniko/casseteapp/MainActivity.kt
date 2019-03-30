package com.syroniko.casseteapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*

import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import org.json.JSONObject


const val spotifyResultExtraName = "Spotify Result Extra Name"
const val spotifyArtistResultExtraName = "Spotify Artist Result Extra Name"
const val tokenExtraName = "Token Extra Name"

class MainActivity : AppCompatActivity() {

    private val clientId = "846a7d470725449994155b664cb7959b"
    private val redirectUri = "https://duckduckgo.com"
    private val tag = MainActivity::class.java.simpleName
    private val spotifyRequestCode = 1337

    private var token: String? = null
    private val tracks = arrayListOf<SpotifyTrack>()
    private val artists = arrayListOf<SpotifyArtist>()
    private val resultsList = arrayListOf<SpotifyResult>()
//    private val query = "curl -X \"GET\" \"https://api.spotify.com/v1/search?q=tania%20bowra&type=artist\" -H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"Authorization: Bearer BQBcWomvWMyNnWEDb6L29ZXQlbx_-MjeifoqBZUT7-lj88nNRlaIxL1ZzHutOgGglnm07bkN_hG-NLKtS1uv6WZYLXNYU1daFypzh2f8qky1gCtpxGohQ2rSkuPSwTJmy8wwaR85MzBzHzYHTqajm99r4l4ldOs3IrcqUPa4SM8JMOp0KSAdU3RE5IUF2Wpdmopy086gDD33cdkTEzlPVyMqcsDRaXDtW837xNFMBBkJkLW3ap9M0Saj3uVYofx3nZg5yZat_1ETE6cwV9D0PEZJxS2VeVYUsNY\""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = FirebaseFirestore.getInstance()
        val queue = Volley.newRequestQueue(this)
        val context = this

        val builder = AuthenticationRequest.Builder(clientId, AuthenticationResponse.Type.TOKEN, redirectUri)
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, spotifyRequestCode, request)


//        trackButton.setOnClickListener {
//            val searchKeyWord = searchEditText.text.toString().replace(" ", "%20")
//
//            val searchQuery: String = if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
//                "https://api.spotify.com/v1/searchTrack?q=$searchKeyWord&type=track"
//            } else {
//                "https://api.spotify.com/v1/searchTrack?q=$searchKeyWord*&type=track"
//            }
////            Log.d(tag, searchQuery)
//            searchTrack(searchQuery, context, queue)
//        }
//
//
//        artistButton.setOnClickListener {
//            val searchKeyWord = searchEditText.text.toString().replace(" ", "%20")
//
//            val searchQuery: String = if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
//                "https://api.spotify.com/v1/search?q=$searchKeyWord&type=artist"
//            } else {
//                "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=artist"
//            }
//
//            searchArtist(searchQuery, context, queue)
//        }


        resultsButton.setOnClickListener {
//            toast("WOW")
            resultsList.clear()
            artists.clear()
            tracks.clear()
            searchTrack(prepareTrackQuery(), context, queue)
            searchArtist(prepareArtistQuery(), context, queue)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == spotifyRequestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d(tag, "token")
                    token = response.accessToken
                }

                AuthenticationResponse.Type.ERROR -> {
                    Log.d(tag, "error")
                    Log.e(tag, response.error)
                }

                AuthenticationResponse.Type.CODE -> {
                    Log.d(tag, "code")
                }

                AuthenticationResponse.Type.EMPTY -> {
                    Log.d(tag, "empty")
                }

                AuthenticationResponse.Type.UNKNOWN -> {
                    Log.d(tag, "unknown")
                }

                null -> {
                    Log.d(tag, "null")
                }
            }
        }
    }

    private fun prepareTrackQuery(): String {
        val searchKeyWord = searchEditText.text.toString().replace(" ", "%20")

        return if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
            "https://api.spotify.com/v1/search?q=$searchKeyWord&type=track"
        } else {
            "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=track"
        }
    }

    private fun prepareArtistQuery(): String{
        val searchKeyWord = searchEditText.text.toString().replace(" ", "%20")

        return if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
            "https://api.spotify.com/v1/search?q=$searchKeyWord&type=artist"
        } else {
            "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=artist"
        }
    }

    private fun searchTrack(searchQueryUrl: String, context: Context, queue: RequestQueue){
        val searchRequest = SearchRequest(
            Request.Method.GET,
            searchQueryUrl,
            token,
            Response.Listener<JSONObject> { response ->

                val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
                val items = (jsonObject.get("tracks") as JsonObject).get("items") as JsonArray
                if(items.size() != 0){
                    resultsList.add(SpotifySeparator("Tracks"))
                }

                for(track in items){
                    val jsonTrack = track as JsonObject
                    val trackName = jsonTrack.get("name").asString

                    val trackId = jsonTrack.get("id").asString

                    val artistIds = arrayListOf<String>()
                    val artistNames = arrayListOf<String>()
                    for(artist in jsonTrack.getAsJsonArray("artists")){
                        artistIds.add((artist as JsonObject).get("id").asString)
                        artistNames.add((artist as JsonObject).get("name").asString)
                    }

                    val imageUrl = (jsonTrack.getAsJsonObject("album").getAsJsonArray("images")[0] as JsonObject).get("url").asString

                    val spotifyTrack = SpotifyTrack(trackName, trackId, artistIds, artistNames, imageUrl)
                    tracks.add(spotifyTrack)
                    resultsList.add(spotifyTrack)
                }

//                resultsList.addAll(tracks)
            },
            Response.ErrorListener { error -> Log.e(tag, "Volley Error", error); context.toast("Error. :("); Log.d(tag, error.toString()) })

        queue.add(searchRequest)
    }

    private fun searchArtist(searchQueryUrl: String, context: Context, queue: RequestQueue){
        val searchRequest = SearchRequest(
            Request.Method.GET,
            searchQueryUrl,
            token,
            Response.Listener<JSONObject> { response ->

                val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
                val items = (jsonObject.get("artists") as JsonObject).get("items") as JsonArray
                if(items.size() != 0){
                    resultsList.add(SpotifySeparator("Artist"))
                }

                for(artist in items){
                    val jsonArtist = artist as JsonObject

                    val artistName = jsonArtist.get("name").asString

                    val artistId = jsonArtist.get("id").asString

                    val imageUrl = if(jsonArtist.getAsJsonArray("images").size() > 0) {
                        (jsonArtist.getAsJsonArray("images")[0] as JsonObject).get("url").asString
                    }
                    else{
                        ""
                    }

                    val spotifyArtist = SpotifyArtist(artistName, artistId, imageUrl)
                    artists.add(spotifyArtist)
                    resultsList.add(spotifyArtist)
                }

//                resultsList.addAll(artists)


                val intent = Intent(context, SpotifyResultActivity::class.java)
                intent.putExtra(spotifyResultExtraName, resultsList)
                intent.putExtra(tokenExtraName, token)
                context.startActivity(intent)
            },
            Response.ErrorListener { error -> Log.e(tag, "Volley Error", error); context.toast("Error. :("); Log.d(tag, error.toString()) })

        queue.add(searchRequest)
    }
}

fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
