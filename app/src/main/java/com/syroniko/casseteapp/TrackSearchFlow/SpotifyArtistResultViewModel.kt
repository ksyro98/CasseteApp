package com.syroniko.casseteapp.TrackSearchFlow

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
import com.syroniko.casseteapp.utils.SPOTIFY_NO_TOKEN
import javax.inject.Inject

class SpotifyArtistResultViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application){

    private val albumList = arrayListOf<SpotifyAlbum>()
    private val queue = Volley.newRequestQueue(application)
    var query = ""
    var user: User = User()

    fun getSpotifyAlbums(callback: (albumList: ArrayList<SpotifyAlbum>) -> Unit){
        if (query == "" || user.spotifyToken == SPOTIFY_NO_TOKEN){
            return
        }

        searchAlbum(query, user.spotifyToken, queue, albumList){
            callback(albumList)
        }
    }
}