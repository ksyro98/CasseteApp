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



/**
 * A simple [Fragment] subclass.
 */
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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////PART OF REMOVING ROOM FROM THE APP, DON'T DELETE UNTIL WE MAKE SURE THAT IT'S A GOOD IDEA///////
            if (false) {
//            val localCassette = LocalCassette(cassetteId, viewModel.trackName, senderId)
//
//            val localDb = context?.let { it1 ->
//                Room.databaseBuilder(
//                    it1,
//                    AppDatabase::class.java, "cassette_database"
//                ).build()
//            }
//            lifecycleScope.launch {
//                localDb?.cassetteDao()?.delete(localCassette)
//            }.invokeOnCompletion {
//                val resultIntent = Intent()
//                resultIntent.putExtra(resultCassette, localCassette)
//                activity?.setResult(resultForward, resultIntent)
//                activity?.finish()
//            }
            }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }

        val activity = activity
        replyButton.setOnClickListener {
            viewModel.sendReplyMessage()
            viewModel.updateOnReply()

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////PART OF REMOVING ROOM FROM THE APP, DON'T DELETE UNTIL WE MAKE SURE THAT IT'S A GOOD IDEA///////
            if (false) {
//            val localDb = context?.let {
//                Room.databaseBuilder(
//                    it,
//                    AppDatabase::class.java, "cassette_database"
//                ).build()
//            }
//
//            val localCassette = LocalCassette(cassetteId, viewModel.trackName, senderId)
//            lifecycleScope.launch {
//                localDb?.cassetteDao()?.delete(localCassette)
//            }
            }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            val resultIntent = Intent()
//            resultIntent.putExtra(resultCassette, localCassette)
            activity?.setResult(resultForward, resultIntent)
            activity?.finish()
        }

        return view
    }

    override fun getInitialCassetteData(cassetteId: String?, senderId: String?) {

    }

    override fun getCassetteDataFromDb(cassetteComment: String, trackName: String, trackId: String, trackPreviewUrl: String) {

    }

}
