package com.syroniko.casseteapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
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
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.TrackSearchFlow.noPreviewUrl
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.android.synthetic.main.activity_cassette_viewer.*
import kotlinx.coroutines.launch
import org.json.JSONArray


const val resultForward = 12
const val resultReply = 11
const val resultCassette = "result cassette"
const val requestTag = "YouTubeTag"
const val ytApiKey = "AIzaSyDK0u16JJxYenpBhQRte-CC5FHl0IcMIeM"
const val ytSearchUrlStart = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
const val ytSearchUrlEnd = "&type=video&key=$ytApiKey"
const val ytAppWatchUrl = "vnd.youtube:"
const val ytWebWatchUrl = "https://www.youtube.com/watch?v="

class CassetteViewerActivity : AppCompatActivity() {
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private var trackPreviewUrl = noPreviewUrl
    private var videoId: String? = null

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

                getYTData(trackName)
//                toast(trackName)
//                toast(trackId)
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

            db.collection("users")
                .document(uid)
                .update("cassettes", FieldValue.arrayRemove(cassetteId))

            val resultIntent = Intent()
            resultIntent.putExtra(resultCassette, localCassette)
            setResult(resultForward, resultIntent)
            finish()
        }


        youTubeImageView.isEnabled = false
        youTubeImageView.setOnClickListener {
            if (videoId != null) {
                val ytAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ytAppWatchUrl + videoId))
                val ytWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ytWebWatchUrl + videoId))
                try {
                    startActivity(ytAppIntent)
                } catch (e: ActivityNotFoundException) {
                    startActivity(ytWebIntent)
                }
            }
            else{
                toast("An error occurred while retrieving YouTube data.")
            }
        }

        youTubeButton.setOnClickListener{
            val ytIntent = Intent(Intent.ACTION_SEARCH)
            ytIntent.setPackage("com.google.android.youtube")
            ytIntent.putExtra("query", trackName)
            ytIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(ytIntent)
        }

    }

    private fun connected(trackId: String){
        val playerApi = spotifyAppRemote.playerApi//.play(" spotify:track:6rqhFgbbKwnb9MLmUQDhG6")

        spotifyAppRemote.userApi.capabilities.setResultCallback { capabilities ->
            if(capabilities.canPlayOnDemand){
                playerApi.play("spotify:track:$trackId")
            }
            else{
                if (trackPreviewUrl != noPreviewUrl) {
                    val uri = Uri.parse(trackPreviewUrl)

                    MediaPlayer.create(this, uri).start()
                }
                else{
                    longToast("This track has no preview available. You need Spotify premium to listen to it.")
                }
            }
        }
    }

    private fun getYTData(trackName: String){
        val queue = Volley.newRequestQueue(this)

        Log.e(CassetteViewerActivity::class.java.simpleName, ytSearchUrlStart + trackName + ytSearchUrlEnd)

        val ytRequest = JsonObjectRequest(
            Request.Method.GET,
            ytSearchUrlStart + trackName.replace(" ", "+") + ytSearchUrlEnd,
            null,
            Response.Listener { response ->
                val items = response["items"] as JSONArray
                val firstSong = items.getJSONObject(0)
                val id = firstSong.getJSONObject("id")
                videoId = id.getString("videoId")
                youTubeImageView.isEnabled = true

                val snippet = firstSong.getJSONObject("snippet")
                val thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("medium")
                val imageUrl = thumbnail.getString("url")
                Log.d(CassetteViewerActivity::class.java.simpleName, imageUrl)
                Glide.with(this).load(imageUrl).into(youTubeImageView)
            },
            Response.ErrorListener {error ->
                Log.e(CassetteViewerActivity::class.java.simpleName, "Something went wrong... :(")
                Log.e(CassetteViewerActivity::class.java.simpleName, error.toString())
            })
        queue.add(ytRequest)
    }
}
