package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.MainClasses.TOKEN_EXTRA_NAME
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_spotify_artist_result.*
import org.json.JSONObject
import javax.inject.Inject

const val SPOTIFY_ARTIST_RESULT_EXTRA_NAME = "Spotify Artist Result Extra Name"

@AndroidEntryPoint
class SpotifyArtistResultActivity : AppCompatActivity() {

    private val viewModel by viewModels<SpotifyArtistResultViewModel>()
    @Inject lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_artist_result)

        if(intent == null){
            return
        }

        val artistName = intent.getStringExtra(SPOTIFY_ARTIST_RESULT_EXTRA_NAME)?.replace(" ", "%20") ?: return
        viewModel.token = intent.getStringExtra(TOKEN_EXTRA_NAME) ?: return

        viewModel.query = "https://api.spotify.com/v1/search?q=artist%3A$artistName&type=album"

        spotifyAlbumRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SpotifyArtistResultActivity, 2)
            setHasFixedSize(true)
            adapter = albumAdapter
        }

        albumAdapter.token = viewModel.token
        viewModel.getSpotifyAlbums { albumAdapter.albumList = it }

    }

    companion object {
        fun startActivity(context: Context, artistName: String, token: String?){
            val artistIntent = Intent(context, SpotifyArtistResultActivity::class.java)
            artistIntent.putExtra(SPOTIFY_ARTIST_RESULT_EXTRA_NAME, artistName)
            artistIntent.putExtra(TOKEN_EXTRA_NAME, token)
            context.startActivity(artistIntent)
        }
    }

}
