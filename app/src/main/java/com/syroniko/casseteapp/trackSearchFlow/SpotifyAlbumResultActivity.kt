package com.syroniko.casseteapp.trackSearchFlow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.syroniko.casseteapp.mainClasses.USER_EXTRA_NAME
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_spotify_album_result.*
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
        viewModel.user = intent.getParcelableExtra(USER_EXTRA_NAME) ?: return
        viewModel.albumImageUrl = intent.getStringExtra(spotifyAlbumImageUrlExtraName) ?: return

        spotifyAlbumResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SpotifyAlbumResultActivity)
            setHasFixedSize(true)
            adapter = trackAdapter
        }

        trackAdapter.user = viewModel.user
        viewModel.getSpotifyData { tracks -> trackAdapter.spotifyItemsList = tracks }
    }

    companion object{
        fun startActivity(context: Context, albumId: String, albumImageUrl: String, user: User){
            val trackIntent = Intent(context, SpotifyAlbumResultActivity::class.java)

            trackIntent.putExtra(spotifyAlbumIdExtraName, albumId)
            trackIntent.putExtra(spotifyAlbumImageUrlExtraName, albumImageUrl)
            trackIntent.putExtra(USER_EXTRA_NAME, user)

            context.startActivity(trackIntent)
        }
    }
}
