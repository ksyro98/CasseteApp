package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.MainClasses.TOKEN_EXTRA_NAME
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyResult
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_spotify_album_result.*
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class SpotifyAlbumResultActivity : AppCompatActivity() {

    private val viewModel by viewModels<SpotifyAlbumResultViewModel>()
    @Inject lateinit var trackAdapter: SpotifyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_album_result)

        if(intent == null){
            return
        }

        viewModel.albumId = intent.getStringExtra(spotifyAlbumIdExtraName) ?: return
        viewModel.token = intent.getStringExtra(TOKEN_EXTRA_NAME) ?: return
        viewModel.albumImageUrl = intent.getStringExtra(spotifyAlbumImageUrlExtraName) ?: return

        spotifyAlbumResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SpotifyAlbumResultActivity)
            setHasFixedSize(true)
            adapter = trackAdapter
        }

        trackAdapter.token = viewModel.token
        viewModel.getSpotifyData { tracks -> trackAdapter.spotifyItemsList = tracks }
    }

    companion object{
        fun startActivity(context: Context, albumId: String, albumImageUrl: String, token: String?){
            val trackIntent = Intent(context, SpotifyAlbumResultActivity::class.java)

            trackIntent.putExtra(spotifyAlbumIdExtraName, albumId)
            trackIntent.putExtra(spotifyAlbumImageUrlExtraName, albumImageUrl)
            trackIntent.putExtra(TOKEN_EXTRA_NAME, token)

            context.startActivity(trackIntent)
        }
    }
}
