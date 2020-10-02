package com.syroniko.casseteapp.TrackSearchFlow

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult

private const val TAG = "TrackSearchNetworkUtils"
private var searchDone = false

fun searchTrack(
    searchQueryUrl: String,
    queue: RequestQueue,
    token: String,
    tracks: ArrayList<SpotifyResult>,
    callback: () -> Unit){

    val searchRequest = SearchRequest(
        Request.Method.GET,
        searchQueryUrl,
        token,
        Response.Listener { response ->
            val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
            val items = (jsonObject.get("tracks") as JsonObject).get("items") as JsonArray

            for (track in items) {
                val spotifyTrack = getTrackJson(track)
                tracks.add(spotifyTrack)
            }

            if (searchDone) callback() else searchDone = true

        },
        Response.ErrorListener { error ->
            Log.e(TAG, "Volley Error", error);
        })

    queue.add(searchRequest)
}

fun searchArtist(
    searchQueryUrl: String,
    queue: RequestQueue,
    token: String,
    artists: ArrayList<SpotifyResult>,
    callback: () -> Unit){

    val searchRequest = SearchRequest(
        Request.Method.GET,
        searchQueryUrl,
        token,
        Response.Listener { response ->

            val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
            val items = (jsonObject.get("artists") as JsonObject).get("items") as JsonArray

            for (artist in items) {
                val spotifyArtist = getArtistJson(artist)
                artists.add(spotifyArtist)
            }

            if (searchDone) callback() else searchDone = true

        },
        Response.ErrorListener { error ->
            Log.e(TAG, "Volley Error", error);
        })

    queue.add(searchRequest)
}


