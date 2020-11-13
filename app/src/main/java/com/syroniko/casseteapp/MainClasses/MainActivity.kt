package com.syroniko.casseteapp.MainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
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
import com.syroniko.casseteapp.ChatAndMessages.FRIEND_ID
import com.syroniko.casseteapp.ChatAndMessages.MessagesFragment
import com.syroniko.casseteapp.CreateCassetteFragment
import com.syroniko.casseteapp.LogInSignUp.WelcomingActivity
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


const val spotifyQueryExtraName = "Spotify Query Extra Name"
const val TOKEN_EXTRA_NAME = "Token Extra Name"
const val CASSETTE_VIEWER_REQUEST_CODE = 314

const val clientId = "846a7d470725449994155b664cb7959b"
const val redirectUri = "https://duckduckgo.com"

const val UID_MAIN_EXTRA = "uid main extra"
const val USER_MAIN_EXTRA = "user main extra"
const val TOKEN_MAIN_EXTRA = "token main extra"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var selectedFragment: Fragment
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(intent.hasExtra(USER_MAIN_EXTRA) && intent.getParcelableExtra<User>(USER_MAIN_EXTRA) != null){
            viewModel.user = intent.getParcelableExtra(USER_MAIN_EXTRA)!!
        }
        if (intent.hasExtra(TOKEN_MAIN_EXTRA) && intent.getStringExtra(TOKEN_MAIN_EXTRA) != null) {
            viewModel.token = intent.getStringExtra(TOKEN_MAIN_EXTRA)!!
        }

        selectedFragment = CassetteCaseFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, selectedFragment).commit()

        bottom_navigation_bar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bot_nav_cassette_case -> selectedFragment = CassetteCaseFragment()
                R.id.bot_nav_messages -> selectedFragment = MessagesFragment()
                R.id.bot_nav_new_cassette -> selectedFragment = CreateCassetteFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, selectedFragment).commit()

            true
        }

        viewModel.setUserOnline()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setUserOffline()
    }

    companion object{
        fun startActivity(context: Context, uid: String?, user: User? = null, token: String? = null){
            val i = Intent(context, MainActivity::class.java)

            i.putExtra(UID_MAIN_EXTRA, uid)
            i.putExtra(USER_MAIN_EXTRA, user)
            i.putExtra(TOKEN_MAIN_EXTRA, token)

            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            context.startActivity(i)
        }
    }

}

fun Context.toast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
