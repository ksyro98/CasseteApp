package com.syroniko.casseteapp.MainClasses

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.activity_main.*

import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.TrackSearchFlow.SpotifyResultActivity
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import com.ybs.countrypicker.CountryPicker
import kotlinx.coroutines.launch


const val spotifyQueryExtraName = "Spotify Query Extra Name"
const val spotifyArtistResultExtraName = "Spotify Artist Result Extra Name"
const val spotifyTrackResultExtraName = "Spotify Track Result Extra Name"
const val tokenExtraName = "Token Extra Name"
const val thirtyMins = 1800000
const val cassetteViewerRequestCode = 314
const val spotifyRequestCode = 1337
const val welcomeRequestCode = 222

const val clientId = "846a7d470725449994155b664cb7959b"
const val redirectUri = "https://duckduckgo.com"

class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName
    private val TAG = MainActivity::class.java.simpleName
    private var token: String? = null
    private val tracks = arrayListOf<SpotifyTrack>()
    private val artists = arrayListOf<SpotifyArtist>()
    private val resultsList = arrayListOf<SpotifyResult>()

    private var searchDone = false
    private var uid = ""
    private val cassettes = mutableListOf<LocalCassette>()
    private val cassetteAdapter = CassetteAdapter(this, cassettes)

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


        val viewManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
//        val cassetteAdapter = CassetteAdapter(cassettes)

        val localDb = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "cassette_database"
        ).build()
        lifecycleScope.launch {
            cassettes += localDb.cassetteDao().getAll()
            cassetteRecyclerView.apply {
                cassetteAdapter.notifyDataSetChanged()
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = cassetteAdapter
            }
        }


    }


    override fun onStart() {
        super.onStart()

        //Firebase Auth
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(this, WelcomingActivity::class.java)
            startActivity(intent)
        }
        else{
            uid = user.uid
//            retrievePastCassettes()

            val db = FirebaseFirestore.getInstance()
//            toast(uid)
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val time = documentSnapshot["receivedLastCassetteAt"] as Long
                    if(System.currentTimeMillis()-time > thirtyMins){
                        getCassettes()
                    }
                }
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
                    Log.e(tag, "spotify error" + response.error)
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
        else if (requestCode == cassetteViewerRequestCode){
            if (resultCode == resultForward){
                val localCassette = data?.getParcelableExtra<LocalCassette>(resultCassette)
                cassettes.remove(localCassette)
                cassetteAdapter.notifyDataSetChanged()
            }
            else if (resultCode == resultReply){
                toast("Start chat!")
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
//            .whereLessThan("receivedLastCassetteAt", System.currentTimeMillis()- thirtyMins)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val cassetteId = document.id
                    val trackMap = (document.data["track"] as Map<*, *>)
                    val trackName = trackMap["trackName"] as String
                    val senderId = (document.data["senderId"] as String)

                    val newCassette = LocalCassette(cassetteId, trackName, senderId)

                    val localDb = Room.databaseBuilder(
                        this,
                        AppDatabase::class.java, "cassette_database"
                    ).build()

                    lifecycleScope.launch {
                        localDb.cassetteDao().insertAll(newCassette)
                    }

                    cassettes += newCassette
                    cassetteAdapter.notifyDataSetChanged()


                    db.collection("cassettes")
                        .document(cassetteId)
                        .update(
                            mapOf(
                                "received" to true
//                              changed this: "track.received" to true to the above, not sure if correct
                            )
                        )


                    db.collection("users")
                        .document(uid)
                        .update(
                            "cassettes",
                            FieldValue.arrayUnion(cassetteId)
                        )

                    db.collection("users")
                        .document(uid)
                        .update(
                            mapOf(
                                "receivedLastCassetteAt" to System.currentTimeMillis()
                            )
                        )

                }

            }
            .addOnFailureListener {
                Log.e(MainActivity::class.java.simpleName, "Retrieving Data Error", it)
            }

    }
}

fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
