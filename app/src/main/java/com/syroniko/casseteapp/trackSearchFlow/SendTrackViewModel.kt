package com.syroniko.casseteapp.trackSearchFlow

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.mainClasses.Cassette
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.spotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.mainClasses.NO_RECEIVER_YET
import com.syroniko.casseteapp.utils.SpotifyAuthRequest
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

class SendTrackViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    lateinit var track: SpotifyTrack
    lateinit var user: User
    private val queue = Volley.newRequestQueue(application)
    private val uid = Auth.getUid()
    private val restrictedReceivers = arrayListOf(uid)


    fun getGenre(callback: (String?) -> Unit){
        SpotifyAuthRequest.getToken(getApplication()) { accessToken ->
            track.getGenreFromArtists(queue, accessToken, callback)
        }
    }


    fun sendCassette(comment: String, callback: () -> Unit){
        val cassette = Cassette(
            uid,
            track,
            comment,
            track.genre,
            NO_RECEIVER_YET,
            restrictedReceivers
        )

        val cassetteSender = CassetteSender(cassette, callback)
        cassetteSender.startCassetteSendingAlgorithm()
    }
}