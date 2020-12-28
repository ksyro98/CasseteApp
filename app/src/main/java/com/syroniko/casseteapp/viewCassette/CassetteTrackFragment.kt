package com.syroniko.casseteapp.viewCassette

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels

import com.syroniko.casseteapp.R

class CassetteTrackFragment : Fragment(), CassetteData {
    private val viewModel by activityViewModels<CassetteViewerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_track, container, false)

        val spotifyPlayButton: Button = view.findViewById(R.id.spotifyPlayButton)
    //    val replyButton: Button = view.findViewById(R.id.replyButton)
      //  val forwardButton: Button = view.findViewById(R.id.forwardButton)

        spotifyPlayButton.isEnabled = viewModel.shouldEnableButton

        spotifyPlayButton.setOnClickListener {
            viewModel.spotifyConnect()
        }
//
//        replyButton.setOnClickListener {
//            viewModel.sendReplyMessage()
//            viewModel.updateOnReply()
//
//            (activity as CassetteViewerActivity).finishWithResult(RESULT_RESPONSE)
//        }
//
//        forwardButton.setOnClickListener {
//            viewModel.updateOnForward()
//
//            (activity as CassetteViewerActivity).finishWithResult(RESULT_FORWARD)
//        }

        return view
    }

}
