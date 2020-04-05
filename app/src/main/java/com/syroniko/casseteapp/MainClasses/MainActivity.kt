package com.syroniko.casseteapp.MainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.syroniko.casseteapp.CassetteCaseFragment
import com.syroniko.casseteapp.ChatAndMessages.MessagesFragment
import com.syroniko.casseteapp.CreateCassetteFragment
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


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

    private val clientId = "846a7d470725449994155b664cb7959b"
    private val redirectUri = "https://duckduckgo.com"
    private val tag = MainActivity::class.java.simpleName
    private val spotifyRequestCode = 1337
    private val welcomeRequestCode = 222

    private val TAG = CoreActivity::class.java.simpleName

    private var uid = ""
    private var db = FirebaseFirestore.getInstance()
    private var token: String? = null
    private val tracks: ArrayList<SpotifyTrack>? = null
    private val artists: ArrayList<SpotifyArtist>? = null
    private val resultsList: ArrayList<SpotifyResult>? = null

    private val searchDone = false
    private val cassettes = ArrayList<LocalCassette>()
    private var selectedFragment: Fragment? = null
//    private var cassetteCaseFragment: CassetteCaseFragment? = null
//    private var messagesFragment: MessagesFragment? = null
//    private var createCassetteFragment: CreateCassetteFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)
//        cassetteCaseFragment = CassetteCaseFragment()
//        messagesFragment = MessagesFragment()
//        createCassetteFragment = CreateCassetteFragment()
        selectedFragment = CassetteCaseFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.core_fragment_container, selectedFragment!!).commit()


        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.bot_nav_cassette_case -> {
                    selectedFragment = CassetteCaseFragment()
                    updateCassettesInView(cassettes)
//                    (selectedFragment as CassetteCaseFragment).updateData(cassettes)
//                    selectedFragment.updateData(cassettes)
                    Log.d(tag, cassettes.toString())
                }
                R.id.bot_nav_messages -> selectedFragment = MessagesFragment()
                R.id.bot_nav_new_cassette -> {
                    selectedFragment = CreateCassetteFragment()
                    (selectedFragment as CreateCassetteFragment).setToken(token)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.core_fragment_container, selectedFragment!!).commit()
            true
        }
        val builder = AuthenticationRequest.Builder(
            com.syroniko.casseteapp.MainClasses.clientId, AuthenticationResponse.Type.TOKEN,
            com.syroniko.casseteapp.MainClasses.redirectUri
        )
        val request = builder.build()
        AuthenticationClient.openLoginActivity(
            this,
            com.syroniko.casseteapp.MainClasses.spotifyRequestCode, request
        )


        val localDb = this.let {
            Room.databaseBuilder(
                it,
                AppDatabase::class.java, "cassette_database"
            ).build()
        }

        lifecycleScope.launch {
            cassettes.addAll(localDb.cassetteDao().getAll())
            updateCassettesInView(cassettes)
//            if(selectedFragment != null && selectedFragment is CassetteCaseFragment){
//                (selectedFragment as CassetteCaseFragment).updateData(cassettes)
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == spotifyRequestCode) {
            val response =
                AuthenticationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d(TAG, "token")
                    token = response.accessToken
                }
                AuthenticationResponse.Type.ERROR -> {
                    Log.d(TAG, "error")
                    Log.e(TAG, "spotify error" + response.error)
                }
                AuthenticationResponse.Type.CODE -> Log.d(TAG, "code")
                AuthenticationResponse.Type.EMPTY -> Log.d(TAG, "empty")
                AuthenticationResponse.Type.UNKNOWN -> Log.d(TAG, "unknown")
                else -> Log.d(TAG, "null")
            }
        }
        else if (requestCode == cassetteViewerRequestCode){
            val localDb = this.let {
                Room.databaseBuilder(
                    it,
                    AppDatabase::class.java, "cassette_database"
                ).build()
            }

            lifecycleScope.launch {
                cassettes.clear()
                cassettes.addAll(localDb.cassetteDao().getAll())
                if(selectedFragment != null && selectedFragment is CassetteCaseFragment){
                    (selectedFragment as CassetteCaseFragment).setNewData(cassettes)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.uid == null) {
            val intent = Intent(this, WelcomingActivity::class.java)
            startActivity(intent)
        } else {
            uid = firebaseAuth.uid!!
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val time = documentSnapshot["receivedLastCassetteAt"] as Long
                    if (System.currentTimeMillis() - time > thirtyMins) {
                        getNewCassette()
                        retrieveCassettes()
                    }
                }
        }
    }

    fun getUid(): String{
        return uid
    }

    private fun getNewCassette() {
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

                    val localDb = this.let {
                        Room.databaseBuilder(
                            it,
                            AppDatabase::class.java, "cassette_database"
                        ).build()
                    }

                    lifecycleScope.launch {
                        localDb.cassetteDao().insertAll(newCassette)
                    }

                    cassettes.add(newCassette)

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

    private fun retrieveCassettes(){
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {documentSnapshot ->
                val userCassettes = documentSnapshot["cassettes"] as ArrayList<*>
                val cassetteIds = cassettes.map { cassette -> cassette.cassetteId }
                for (userCassette in userCassettes){
                    if (userCassette !in cassetteIds){
                        db.collection("cassettes")
                            .document(userCassette.toString())
                            .get()
                            .addOnSuccessListener { cassetteDocument ->
                                val trackMap = cassetteDocument["track"] as Map<*, *>
                                val trackName = trackMap["trackName"] as String
                                val senderId = cassetteDocument["senderId"] as String
                                val localCassette = LocalCassette(userCassette.toString(), trackName, senderId)
                                cassettes.add(localCassette)
                                updateCassettesInView(mutableListOf(localCassette))
                            }
                    }
                }
            }
            .addOnFailureListener {
                toast("Unable to retrieve cassettes from our server.")
            }
    }

    private fun updateCassettesInView(newCassettes: MutableList<LocalCassette>){
        if (selectedFragment != null && selectedFragment is CassetteCaseFragment){
            (selectedFragment as CassetteCaseFragment).updateData(newCassettes)
        }
    }
}

fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

