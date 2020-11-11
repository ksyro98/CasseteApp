package com.syroniko.casseteapp.viewCassette

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.syroniko.casseteapp.ChatAndMessages.sendFirstMessage
import com.syroniko.casseteapp.ChatAndMessages.sendMessage
import com.syroniko.casseteapp.MainClasses.clientId
import com.syroniko.casseteapp.MainClasses.longToast
import com.syroniko.casseteapp.MainClasses.redirectUri
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.TrackSearchFlow.NO_PREVIEW_URL
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.ChatDB
import com.syroniko.casseteapp.firebase.UserDB
import kotlinx.android.synthetic.main.fragment_cassette_video.*
import org.json.JSONArray
import javax.inject.Inject

class CassetteViewerViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    /**
     * All Fragments/Activity
     */
    var trackPreviewUrl = NO_PREVIEW_URL
    lateinit var cassetteId: String
    lateinit var senderId: String
    lateinit var trackId: String
    lateinit var cassetteComment: String


    /**
     * All Fragments/Activity
     */
    fun getCassette(callback: () -> Unit) {
        CassetteDB.getDocumentFromId(cassetteId)
            .addOnSuccessListener { document ->
                cassetteComment = document.data?.get("comment") as String

                val trackMap = document.data?.get("track") as Map<*, *>
                trackName = trackMap["trackName"] as String
                trackId = trackMap["trackId"] as String
                if (trackMap["previewUrl"] != null) {
                    trackPreviewUrl = trackMap["previewUrl"] as String
                }

                callback()
            }
    }


    /**
     * CassetteMessageFragment Variables
     */
    lateinit var trackName: String
    lateinit var senderName: String
    private val uid = Auth.getUid()


    /**
     * CassetteMessageFragment Functions
     */
    fun getSender(callback: (String) -> Unit) {
        UserDB.getDocumentFromId(senderId)
            .addOnSuccessListener { document ->
                senderName = document.data?.get("name") as String
                callback(senderName)
            }
    }

    fun updateOnForward() {
        if (uid == null) {
            Log.e(CassetteViewerViewModel::class.simpleName, "uid is null")
            return
        }

        val updateMap = hashMapOf<String, Any>(
            Pair("possibleReceivers", FieldValue.arrayRemove(uid)),
            Pair("restrictedReceivers", FieldValue.arrayUnion(uid))
        )
        CassetteDB.update(cassetteId, updateMap)

        CassetteDB.update(cassetteId, hashMapOf(Pair("received", false)))

        UserDB.update(uid, hashMapOf(Pair("cassettes", FieldValue.arrayRemove(cassetteId))))
    }

    fun updateOnReply() {
        if (uid == null) {
            Log.e(CassetteViewerViewModel::class.simpleName, "uid is null")
            return
        }

        UserDB.update(uid, hashMapOf(Pair("friends", FieldValue.arrayUnion(senderId))))
        UserDB.update(uid, hashMapOf(Pair("cassettes", FieldValue.arrayRemove(cassetteId))))
    }

    fun sendReplyMessage() {
        if (uid == null) {
            Log.e(CassetteViewerViewModel::class.simpleName, "uid is null")
            return
        }

        //TODO change that to server time
        sendFirstMessage(
            uid,
            senderId,
            "Hello, I received your cassette with $trackName and I loved it!"
        )
    }

    /**
     * CassetteTrackFragment Variables
     */

    lateinit var spotifyAppRemote: SpotifyAppRemote
    var shouldEnableButton = true   //false (changed to true)

    /**
     * CassetteTrackFragment Functions
     */
    private fun connectParams(): ConnectionParams = ConnectionParams.Builder(clientId)
        .setRedirectUri(redirectUri)
        .showAuthView(true)
        .build()


    private fun connected(trackId: String) {
        val playerApi = spotifyAppRemote.playerApi
        spotifyAppRemote.userApi.capabilities.setResultCallback { capabilities ->
            if (capabilities.canPlayOnDemand) {
                playerApi.play("spotify:track:$trackId")
            } else {
                if (trackPreviewUrl != NO_PREVIEW_URL) {
                    val uri = Uri.parse(trackPreviewUrl)

                    MediaPlayer.create(getApplication(), uri).start()
                } else {
                    Toast.makeText(getApplication(), "This track has no preview available. You need Spotify premium to listen to it.\n", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun spotifyConnect() {
        SpotifyAppRemote.connect(getApplication(), connectParams(),
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

    /**
     * CassetteVideoFragment Variables
     */
    var videoId: String? = null


    /**
     * CassetteVideoFragment Functions
     */
    fun getTrackName(callback: (String) -> Unit) {
//        if(viewModel.trackName != null) {
        getYTData(trackName,callback)
 //   }
//    else{
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("cassettes")
//            .document(viewModel.cassetteId)
//            .get()
//            .addOnSuccessListener { document ->
//                val trackMap = document.data?.get("track") as Map<*, *>
//                viewModel.trackName = trackMap["trackName"] as String
//
//        getYTData(viewModel.trackName!!)
//    }
//}
    }

    private fun getYTData(trackName: String,callback: (String) -> Unit){
        val queue = Volley.newRequestQueue(getApplication())

        Log.e(CassetteViewerActivity::class.java.simpleName, ytSearchUrlStart + trackName + ytSearchUrlEnd)

        val ytRequest = JsonObjectRequest(
            Request.Method.GET,
            ytSearchUrlStart + trackName.replace(" ", "+") + ytSearchUrlEnd,
            null,
            { response ->
                val items = response["items"] as JSONArray
                val firstSong = items.getJSONObject(0)
                val id = firstSong.getJSONObject("id")
                videoId = id.getString("videoId")

                val snippet = firstSong.getJSONObject("snippet")
                val thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("medium")
                val imageUrl = thumbnail.getString("url")
                Log.d(CassetteViewerActivity::class.java.simpleName, imageUrl)
                callback(imageUrl)
            },
            { error ->
                Log.e(CassetteViewerActivity::class.java.simpleName, "Something went wrong... :(")
                Log.e(CassetteViewerActivity::class.java.simpleName, error.toString())
            }
        )
        queue.add(ytRequest)
    }

    fun makeWebIntent(callback: (Intent) -> Unit) {
        if (videoId != null) {

            val ytAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ytAppWatchUrl + videoId))
            val ytWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ytWebWatchUrl + videoId))

            try {
                callback(ytAppIntent)
            }
            catch (e: ActivityNotFoundException) {
                callback(ytWebIntent)
            }
        }
        else {
            getApplication<Application>().toast("An error occurred while retrieving YouTube data.")
        }
    }

    fun ytResultsQuery() {
        val ytIntent = Intent(Intent.ACTION_SEARCH)
        ytIntent.setPackage("com.google.android.youtube")
        ytIntent.putExtra("query", trackName)
        ytIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        getApplication<Application>().startActivity(ytIntent)
    }
}