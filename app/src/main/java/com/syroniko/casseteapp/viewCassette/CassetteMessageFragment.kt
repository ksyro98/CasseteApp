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
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.syroniko.casseteapp.ChatAndMessages.sendMessage
import com.syroniko.casseteapp.MainClasses.*

import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.noPreviewUrl
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.android.synthetic.main.fragment_cassette_message.*
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
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private var trackPreviewUrl = noPreviewUrl

    private lateinit var spotifyPlayButton: Button
    private lateinit var forwardButton: Button
    private lateinit var replyButton: Button
    private lateinit var cassetteCommentTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_message, container, false)

        forwardButton = view.findViewById(R.id.forwardButton)
        spotifyPlayButton = view.findViewById(R.id.spotifyPlayButton)
        replyButton = view.findViewById(R.id.replyButton)
        cassetteCommentTextView = view.findViewById(R.id.cassetteCommentTextView)
        val senderNameTextView = view.findViewById<TextView>(R.id.senderNameTextView)

        val db = FirebaseFirestore.getInstance()

        spotifyPlayButton.isEnabled = false;
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
                        spotifyPlayButton.isEnabled = true
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


        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()


        forwardButton.setOnClickListener {
            db.collection("cassettes")
                .document(cassetteId)
                .update(
                    "possibleReceivers", FieldValue.arrayRemove(uid)
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
            }

            val resultIntent = Intent()
            resultIntent.putExtra(resultCassette, localCassette)
            activity?.setResult(resultForward, resultIntent)
            activity?.finish()
        }

        spotifyPlayButton.setOnClickListener {

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

        replyButton.setOnClickListener {
            sendMessage(
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
        this.trackName = trackName
        this.trackId = trackId
        this.trackPreviewUrl = trackPreviewUrl

        cassetteCommentTextView.text = cassetteComment
        spotifyPlayButton.isEnabled = true
    }

}
