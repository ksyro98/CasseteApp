package com.syroniko.casseteapp.TrackSearchFlow

import android.app.Application
import android.app.SharedElementCallback
import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import javax.inject.Inject

class SpotifyAlbumResultViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application)  {

    private val trackList = arrayListOf<SpotifyResult>()
    private var query = ""
    var albumId = ""
        set(value) {
            field = value
            query = "https://api.spotify.com/v1/albums/$value/tracks"
        }
    var albumImageUrl = ""
    var user = User()
    private val queue = Volley.newRequestQueue(application)

    fun getSpotifyData(callback: (ArrayList<SpotifyResult>) -> Unit){
        searchTracksFromAlbum(query, queue, user.spotifyToken, albumImageUrl, trackList) {
            callback(trackList)
        }
    }
}