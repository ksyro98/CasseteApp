package com.syroniko.casseteapp.TrackSearchFlow

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.Cassette
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.UserDB
import kotlinx.android.synthetic.main.activity_send_track.*
import javax.inject.Inject

class SendTrackViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    lateinit var track: SpotifyTrack
    lateinit var token: String
    private val queue = Volley.newRequestQueue(application)
    private val uid = Auth.getUid()
    private val restrictedReceivers = arrayListOf(uid)


    fun getGenre(callback: (String?) -> Unit){
        track.getGenreFromArtists(queue, token, callback)
    }


    fun sendCassette(comment: String, callback: () -> Unit){
        UserDB.getPossibleReceivers(track.genre!!, restrictedReceivers) { possibleReceivers ->
            val cassette = Cassette(
                uid,
                track,
                comment,
                track.genre,
                possibleReceivers,
                restrictedReceivers)

            CassetteDB.insert(cassette)

            callback()
        }
    }
}