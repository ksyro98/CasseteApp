package com.syroniko.casseteapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.syroniko.casseteapp.ChatAndMessages.sendMessage
import com.syroniko.casseteapp.MainClasses.clientId
import com.syroniko.casseteapp.MainClasses.longToast
import com.syroniko.casseteapp.MainClasses.redirectUri
import com.syroniko.casseteapp.TrackSearchFlow.noPreviewUrl
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.android.synthetic.main.activity_cassette_viewer.*
import kotlinx.coroutines.launch


const val resultForward = 12
const val resultReply = 11
const val resultCassette = "result cassette"

class CassetteViewerActivity : AppCompatActivity() {
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private var trackPreviewUrl = noPreviewUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cassette_viewer)

        spotifyPlayButton.isEnabled = false

        val cassetteId = intent.getStringExtra(cassetteIdExtraName)
        val senderId = intent.getStringExtra(userIdExtraName)
        val uid = FirebaseAuth.getInstance().uid.toString()
        var trackName = ""
        var trackId = ""


        val db = FirebaseFirestore.getInstance()

        db.collection("cassettes")
            .document(cassetteId)
            .get()
            .addOnSuccessListener { document ->
                val cassetteComment = document.data?.get("comment") as String
                cassetteCommentTextView.text = cassetteComment

                val trackMap = document.data?.get("track") as Map<*, *>
                trackName = trackMap["trackName"] as String
                trackId = trackMap["trackId"] as String
                if (trackId != ""){
                    spotifyPlayButton.isEnabled = true
                }
                if(trackMap["previewUrl"] != null) {
                    trackPreviewUrl = trackMap["previewUrl"] as String
                }
//                toast(trackName)
//                toast(trackId)
            }


        Log.d(CassetteViewerActivity::class.java.simpleName, senderId)

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

            val localDb = Room.databaseBuilder(
                this,
                AppDatabase::class.java, "cassette_database"
            ).build()
            lifecycleScope.launch {
                localDb.cassetteDao().delete(localCassette)
            }

            val resultIntent = Intent()
            resultIntent.putExtra(resultCassette, localCassette)
            setResult(resultForward, resultIntent)
            finish()
        }

        replyButton.setOnClickListener {
            val resultIntent = Intent()
            setResult(resultReply, resultIntent)
            finish()
        }


        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()


        spotifyPlayButton.setOnClickListener {

            SpotifyAppRemote.connect(this, connectionParams,
                object : Connector.ConnectionListener {

                    override fun onConnected(appRemote: SpotifyAppRemote) {
                        spotifyAppRemote = appRemote
                        Log.d("MainActivity", "Connected! Yay!")

                        // Now you can start interacting with App Remote
                        connected(trackId)
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

            val localDb = this.let {
                Room.databaseBuilder(
                    it,
                    AppDatabase::class.java, "cassette_database"
                ).build()
            }

            val localCassette = LocalCassette(cassetteId, trackName, senderId)
            lifecycleScope.launch {
                localDb.cassetteDao().delete(localCassette)
            }

            db.collection("users")
                .document(uid)
                .update(
                    "friends",
                    FieldValue.arrayUnion(senderId)
                )

            val resultIntent = Intent()
            resultIntent.putExtra(resultCassette, localCassette)
            setResult(resultForward, resultIntent)
            finish()
        }

    }

    private fun connected(trackId: String){
//        toast(trackId)
//        spotifyAppRemote.playerApi.play("spotify:track:$trackId")
//        if()

        val playerApi = spotifyAppRemote.playerApi//.play(" spotify:track:6rqhFgbbKwnb9MLmUQDhG6")

        spotifyAppRemote.userApi.capabilities.setResultCallback { capabilities ->
            if(capabilities.canPlayOnDemand){
                playerApi.play("spotify:track:$trackId")
            }
            else{
//                val builder = Uri.Builder()
//                builder
//                    .scheme("https")
//                    .authority("p.scdn.co")
//                    .appendPath("mp3-preview")
//                    .appendPath("3eb16018c2a700240e9dfb8817b6f2d041f15eb1")
//                    .appendQueryParameter("cid", "774b29d4f13844c495f206cafdad9c86")

//                val uri = builder.build()

                if (trackPreviewUrl != noPreviewUrl) {
                    val uri = Uri.parse(trackPreviewUrl)

                    MediaPlayer.create(this, uri).start()
                }
                else{
                    longToast("This track has no preview available. You need Spotify premium to listen to it.")
                }
            }
        }
//        toast(trackId)
    }
}
