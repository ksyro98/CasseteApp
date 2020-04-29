package com.syroniko.casseteapp.viewCassette

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.syroniko.casseteapp.MainClasses.clientId
import com.syroniko.casseteapp.MainClasses.longToast
import com.syroniko.casseteapp.MainClasses.redirectUri

import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.noPreviewUrl

class CassetteTrackFragment : Fragment(), CassetteData {

    private var spotifyPlayButton: Button? = null
    private lateinit var spotifyAppRemote: SpotifyAppRemote

    private var cassetteId = ""
    private var senderId = ""
    private var trackId: String? = null
    private var trackPreviewUrl: String? = null
    private var shouldEnableButton = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_track, container, false)

        spotifyPlayButton = view.findViewById(R.id.spotifyPlayButton)
        spotifyPlayButton!!.isEnabled = shouldEnableButton

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        spotifyPlayButton!!.setOnClickListener {

            SpotifyAppRemote.connect(context, connectionParams,
                object : Connector.ConnectionListener {

                    override fun onConnected(appRemote: SpotifyAppRemote) {
                        spotifyAppRemote = appRemote
                        Log.d("MainActivity", "Connected! Yay!")

                        // Now you can start interacting with App Remote
                        trackId?.let { it1 -> connected(it1) }
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                })
        }

        return view
    }


    private fun connected(trackId: String){
        val playerApi = spotifyAppRemote.playerApi

        spotifyAppRemote.userApi.capabilities.setResultCallback { capabilities ->
            if(capabilities.canPlayOnDemand){
                playerApi.play("spotify:track:$trackId")
            }
            else{
                if (trackPreviewUrl != noPreviewUrl) {
                    val uri = Uri.parse(trackPreviewUrl)

                    MediaPlayer.create(context, uri).start()
                }
                else{
                    context?.longToast("This track has no preview available. You need Spotify premium to listen to it.")
                }
            }
        }
    }

    override fun getInitialCassetteData(cassetteId: String?, senderId: String?) {
        this.cassetteId = cassetteId.toString()
        this.senderId = senderId.toString()
    }

    override fun getCassetteDataFromDb(cassetteComment: String, trackName: String, trackId: String, trackPreviewUrl: String) {
        this.trackId = trackId
        this.trackPreviewUrl = trackPreviewUrl

        if (spotifyPlayButton != null) {
            spotifyPlayButton!!.isEnabled = true
        }
        else{
            shouldEnableButton = true
        }
    }

}
