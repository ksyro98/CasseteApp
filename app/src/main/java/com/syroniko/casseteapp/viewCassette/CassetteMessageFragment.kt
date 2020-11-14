package com.syroniko.casseteapp.viewCassette

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels

import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.cassetteIdExtraName


class CassetteMessageFragment : Fragment(), CassetteData {

    private val viewModel by activityViewModels<CassetteViewerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_message, container, false)

        val forwardButton = view.findViewById<Button>(R.id.forwardButton)
        val replyButton = view.findViewById<Button>(R.id.replyButton)
        val cassetteCommentTextView = view.findViewById<TextView>(R.id.cassetteCommentTextView)
        val senderNameTextView = view.findViewById<TextView>(R.id.senderNameTextView)

        cassetteCommentTextView.text = viewModel.cassetteComment

        viewModel.getSender { senderName ->
            senderNameTextView.text = senderName
        }

        forwardButton.setOnClickListener {
            viewModel.updateOnForward()

            (activity as CassetteViewerActivity).finishWithResult(RESULT_FORWARD)
        }

        replyButton.setOnClickListener {
            viewModel.sendReplyMessage()
            viewModel.updateOnReply()

            (activity as CassetteViewerActivity).finishWithResult(RESULT_RESPONSE)
        }

        return view
    }


}
