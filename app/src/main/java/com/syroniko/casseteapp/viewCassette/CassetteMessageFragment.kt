package com.syroniko.casseteapp.viewCassette

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.ChatAndMessages.sendMessage

import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.noPreviewUrl
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.coroutines.launch



/**
 * A simple [Fragment] subclass.
 */
class CassetteMessageFragment : Fragment(), CassetteData {

    private var cassetteId = ""
    private var senderId = ""
    private var uid = FirebaseAuth.getInstance().uid.toString()
    private var trackName = ""
    private var trackId: String? = null
    private var trackPreviewUrl = noPreviewUrl

    private lateinit var forwardButton: Button
    private lateinit var replyButton: Button
    private lateinit var cassetteCommentTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_message, container, false)

        forwardButton = view.findViewById(R.id.forwardButton)
        replyButton = view.findViewById(R.id.replyButton)
        cassetteCommentTextView = view.findViewById(R.id.cassetteCommentTextView)
        val senderNameTextView = view.findViewById<TextView>(R.id.senderNameTextView)

        val db = FirebaseFirestore.getInstance()

        forwardButton.isEnabled = false;
        replyButton.isEnabled = false;

        if(trackId == null) {
            db.collection("cassettes")
                .document(cassetteId)
                .get()
                .addOnSuccessListener { document ->
                    val cassetteComment = document.data?.get("comment") as String
                    cassetteCommentTextView.text = cassetteComment

                    val trackMap = document.data?.get("track") as Map<*, *>
                    trackName = trackMap["trackName"] as String
                    trackId = trackMap["trackId"] as String
                    if (trackId != "") {
                        forwardButton.isEnabled = true
                        replyButton.isEnabled = true
                    }
                    if (trackMap["previewUrl"] != null) {
                        trackPreviewUrl = trackMap["previewUrl"] as String
                    }
                }
        }


        db.collection("users")
            .document(senderId)
            .get()
            .addOnSuccessListener { document ->
                val senderName = document.data?.get("name") as String
                senderNameTextView.text = senderName
            }


        forwardButton.setOnClickListener {
            db.collection("cassettes")
                .document(cassetteId)
                .update(
                    "possibleReceivers", FieldValue.arrayRemove(uid),
                    "restrictedReceivers", FieldValue.arrayUnion(uid)
                )

            db.collection("cassettes")
                .document(cassetteId)
                .update("received", false)

            db.collection("users")
                .document(uid)
                .update(
                    "cassettes", FieldValue.arrayRemove(cassetteId)
                )

            val localCassette = LocalCassette(cassetteId, trackName, senderId)

            val localDb = context?.let { it1 ->
                Room.databaseBuilder(
                    it1,
                    AppDatabase::class.java, "cassette_database"
                ).build()
            }
            lifecycleScope.launch {
                localDb?.cassetteDao()?.delete(localCassette)
            }.invokeOnCompletion {
                val resultIntent = Intent()
                resultIntent.putExtra(resultCassette, localCassette)
                activity?.setResult(resultForward, resultIntent)
                activity?.finish()
            }
        }

        val activity = activity
        replyButton.setOnClickListener {
            sendMessage(
                activity,
                uid,
                senderId,
                "Hello, I received your cassette with $trackName and I loved it!",
                System.currentTimeMillis().toString(),
                db)

            val localDb = context?.let {
                Room.databaseBuilder(
                    it,
                    AppDatabase::class.java, "cassette_database"
                ).build()
            }

            val localCassette = LocalCassette(cassetteId, trackName, senderId)
            lifecycleScope.launch {
                localDb?.cassetteDao()?.delete(localCassette)
            }

            db.collection("users")
                .document(uid)
                .update(
                    "friends",
                    FieldValue.arrayUnion(senderId)
                )

            db.collection("users")
                .document(uid)
                .update("cassettes", FieldValue.arrayRemove(cassetteId))

            val resultIntent = Intent()
            resultIntent.putExtra(resultCassette, localCassette)
            activity?.setResult(resultForward, resultIntent)
            activity?.finish()
        }

        return view
    }

    override fun getInitialCassetteData(cassetteId: String?, senderId: String?) {
        this.cassetteId = cassetteId.toString()
        this.senderId = senderId.toString()
    }

    override fun getCassetteDataFromDb(cassetteComment: String, trackName: String, trackId: String, trackPreviewUrl: String) {
        this.trackName = trackName
        this.trackId = trackId
        this.trackPreviewUrl = trackPreviewUrl

        cassetteCommentTextView.text = cassetteComment
    }

}
