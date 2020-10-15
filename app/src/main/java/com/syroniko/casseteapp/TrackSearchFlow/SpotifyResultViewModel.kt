package com.syroniko.casseteapp.TrackSearchFlow

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifySeparator
import javax.inject.Inject


class SpotifyResultViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    private val resultsList = arrayListOf<SpotifyResult>()
    private val tracks = arrayListOf<SpotifyResult>()
    private val artists = arrayListOf<SpotifyResult>()
    private val queue = Volley.newRequestQueue(application)
    var searchQuery: String = ""
    var token: String = ""



    fun getSpotifyData(callback: (ArrayList<SpotifyResult>) -> Unit){
        if (searchQuery == "" || token == ""){
            return
        }

        val trackQuery = prepareTrackQuery(searchQuery)
        searchTrack(trackQuery, queue, token, tracks) {
            updateResultsList(callback)
        }

        val artistQuery = prepareArtistQuery(searchQuery)
        searchArtist(artistQuery, queue, token, artists) {
            updateResultsList(callback)
        }
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


    private fun updateResultsList(callback: (ArrayList<SpotifyResult>) -> Unit){
        resultsList.clear()
        if(tracks.size != 0) {
            resultsList.add(SpotifySeparator("Tracks"))
        }
        resultsList.addAll(tracks)
        if (artists.size != 0) {
            resultsList.add(SpotifySeparator("Artists"))
        }
        resultsList.addAll(artists)
        callback(resultsList)
    }

}