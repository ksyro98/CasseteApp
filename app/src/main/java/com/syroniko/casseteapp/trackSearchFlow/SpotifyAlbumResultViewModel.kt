package com.syroniko.casseteapp.trackSearchFlow

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.spotifyClasses.SpotifyResult
import com.syroniko.casseteapp.utils.SpotifyAuthRequest
import net.openid.appauth.AuthorizationService
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
        SpotifyAuthRequest.getToken(getApplication()) { accessToken ->
            searchTracksFromAlbum(query, queue, accessToken, albumImageUrl, trackList) {
                callback(trackList)
            }
        }
    }
}