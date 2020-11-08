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
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.syroniko.casseteapp.MainClasses.clientId
import com.syroniko.casseteapp.MainClasses.longToast
import com.syroniko.casseteapp.MainClasses.redirectUri

import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.NO_PREVIEW_URL

class CassetteTrackFragment : Fragment(), CassetteData {
    private var spotifyPlayButton: Button? = null
    private val viewModel by activityViewModels<CassetteViewerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_track, container, false)

        spotifyPlayButton = view.findViewById(R.id.spotifyPlayButton)
        spotifyPlayButton!!.isEnabled = viewModel.shouldEnableButton


        val connectionParams = viewModel.connectParams()
        spotifyPlayButton!!.setOnClickListener {
            viewModel.spotifyConnect()

        }

        return view
    }


    override fun getInitialCassetteData(cassetteId: String?, senderId: String?) {
//        viewModel.cassetteId = cassetteId.toString()
//        viewModel.senderId = senderId.toString()
    }

    override fun getCassetteDataFromDb(cassetteComment: String, trackName: String, trackId: String, trackPreviewUrl: String) {
//        viewModel.trackId = trackId
//        viewModel.trackPreviewUrl = trackPreviewUrl

//        if (spotifyPlayButton != null) {
//            spotifyPlayButton!!.isEnabled = true
//        }
//        else{
//            viewModel.shouldEnableButton = true
//        }
    }

}
