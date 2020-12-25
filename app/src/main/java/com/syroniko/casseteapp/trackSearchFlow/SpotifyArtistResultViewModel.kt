package com.syroniko.casseteapp.trackSearchFlow

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.spotifyClasses.SpotifyAlbum
import com.syroniko.casseteapp.utils.SpotifyAuthRequest
import net.openid.appauth.AuthorizationService
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
        if (query == ""){
            return
        }

        SpotifyAuthRequest.getToken(getApplication()){ accessToken ->
            searchAlbum(query, accessToken, queue, albumList){
                callback(albumList)
            }
        }
    }
}