package com.syroniko.casseteapp.MainClasses

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.activity_main.*

import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifySeparator
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.TrackSearchFlow.SearchRequest
import com.syroniko.casseteapp.TrackSearchFlow.SpotifyResultActivity
import kotlinx.android.synthetic.main.activity_send_track.*
import org.json.JSONObject


const val spotifyQueryExtraName = "Spotify Query Extra Name"
const val spotifyArtistResultExtraName = "Spotify Artist Result Extra Name"
const val spotifyTrackResultExtraName = "Spotify Track Result Extra Name"
const val tokenExtraName = "Token Extra Name"

class MainActivity : AppCompatActivity() {

    private val clientId = "846a7d470725449994155b664cb7959b"
    private val redirectUri = "https://duckduckgo.com"
    private val tag = MainActivity::class.java.simpleName
    private val spotifyRequestCode = 1337
    private val welcomeRequestCode = 222
    private val TAG = MainActivity::class.java.simpleName

    private var token: String? = null
    private val tracks = arrayListOf<SpotifyTrack>()
    private val artists = arrayListOf<SpotifyArtist>()
    private val resultsList = arrayListOf<SpotifyResult>()

    private var searchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this

        //Spotify Auth
        val builder =
            AuthenticationRequest.Builder(clientId, AuthenticationResponse.Type.TOKEN, redirectUri)
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, spotifyRequestCode, request)

        resultsButton.isEnabled = false

        resultsButton.setOnClickListener {
            //TODO some times this crushes, find why when log works
            val searchQuery = searchEditText.text.toString()
            val intent = Intent(context, SpotifyResultActivity::class.java)
            intent.putExtra(spotifyQueryExtraName, searchQuery)
            intent.putExtra(tokenExtraName, token)
            context.startActivity(intent)
        }

        logOutButton.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
        }

        getCassettes()
        receiverChooserBadedOnLastCassetteReceived()
    }

    override fun onStart() {
        super.onStart()

        //Firebase Auth
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(this, WelcomingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == spotifyRequestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d(tag, "token")
                    token = response.accessToken
                    resultsButton.isEnabled = true
                }

                AuthenticationResponse.Type.ERROR -> {
                    Log.d(tag, "error")
                    Log.e(tag, response.error)
                }

                AuthenticationResponse.Type.CODE -> {
                    Log.d(tag, "code")
                }

                AuthenticationResponse.Type.EMPTY -> {
                    Log.d(tag, "empty")
                }

                AuthenticationResponse.Type.UNKNOWN -> {
                    Log.d(tag, "unknown")
                }

                null -> {
                    Log.d(tag, "null")
                }
            }
        }
    }


    private fun getCassettes() {
        if (FirebaseAuth.getInstance().uid == null) {
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("cassettes")
            .whereArrayContains("possibleReceivers", FirebaseAuth.getInstance().uid as String)
            .whereEqualTo("received", false)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val casseteId = document.id
                    val trackMap = (document.data["track"] as Map<*, *>)
                    val trackName = trackMap["trackName"] as String
                    toast(trackName)


                    db.collection("cassettes")
                        .document(casseteId)
                        .update(
                            mapOf(
                                "track.received" to true
                            )
                        )
                }

            }
            .addOnFailureListener {
                Log.e(MainActivity::class.java.simpleName, "Retrieving Data Error", it)
            }

    }


    private fun receiverChooserBadedOnLastCassetteReceived() {
        val db = FirebaseFirestore.getInstance()

        val userReference = db.collection("users")
        userReference.orderBy("receivedLastCassetteAt").limit(1).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    toast(userId)
                }
            }
    }
    }

    fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

