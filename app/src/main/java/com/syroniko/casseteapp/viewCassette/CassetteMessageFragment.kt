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
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.ChatAndMessages.sendMessage

import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.coroutines.launch


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
        }

        val activity = activity
        replyButton.setOnClickListener {
            viewModel.sendReplyMessage()
            viewModel.updateOnReply()

            val resultIntent = Intent()
            activity?.setResult(resultForward, resultIntent)
            activity?.finish()
        }

        return view
    }

}
