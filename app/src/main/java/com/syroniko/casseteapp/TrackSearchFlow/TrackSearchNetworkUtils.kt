package com.syroniko.casseteapp.TrackSearchFlow

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
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
        { response ->
            val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
            val items = (jsonObject.get("tracks") as JsonObject).get("items") as JsonArray

            items.map { item -> tracks.add(getTrackFromJson(item)) }

            if (searchDone) callback() else searchDone = true

        },
        { error ->
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

            items.map { item -> artists.add(getArtistFromJson(item)) }

            if (searchDone) callback() else searchDone = true

        },
        Response.ErrorListener { error ->
            Log.e(TAG, "Volley Error", error);
        })

    queue.add(searchRequest)
}

fun searchAlbum(
    query: String,
    token: String,
    queue: RequestQueue,
    albumList: ArrayList<SpotifyAlbum>,
    callback: () -> Unit
){
    val searchRequest = SearchRequest(
        Request.Method.GET,
        query,
        token,
        Response.Listener { response ->

            val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
            val items = (jsonObject.get("albums") as JsonObject).get("items") as JsonArray

            items.map { item -> albumList.add(getAlbumFromJson(item)) }

            callback()

        },
        Response.ErrorListener { error ->
            Log.e(TAG,"An error occurred while performing this request", error)
        })

    queue.add(searchRequest)
}


fun searchTracksFromAlbum(
    query: String,
    queue: RequestQueue,
    token: String,
    albumImageUrl: String,
    tracks: ArrayList<SpotifyResult>,
    callback: () -> Unit){

    val searchRequest = SearchRequest(
        Request.Method.GET,
        query,
        token,
        Response.Listener { response ->

            val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
            val items = jsonObject.get("items") as JsonArray

            items.map { item -> tracks.add(getTrackFromJson(item, albumImageUrl)) }

            callback()
        },
        Response.ErrorListener { error ->
            Log.e(TAG, "Network Error", error);
        }
    )

    queue.add(searchRequest)
}
