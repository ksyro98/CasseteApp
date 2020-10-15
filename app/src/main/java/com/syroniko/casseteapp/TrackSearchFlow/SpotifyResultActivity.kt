package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.syroniko.casseteapp.MainClasses.spotifyQueryExtraName
import com.syroniko.casseteapp.MainClasses.TOKEN_EXTRA_NAME
import com.syroniko.casseteapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_spotify_result.*
import javax.inject.Inject


@AndroidEntryPoint
class SpotifyResultActivity : AppCompatActivity() {

    private val viewModel by viewModels<SpotifyResultViewModel>()
    @Inject lateinit var spotifyAdapter: SpotifyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_result)

        viewModel.searchQuery = intent.getStringExtra(spotifyQueryExtraName) ?: return
        viewModel.token = intent.getStringExtra(TOKEN_EXTRA_NAME) ?: return

        spotifyAdapter.token = viewModel.token

        spotifyResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SpotifyResultActivity)
            setHasFixedSize(true)
            adapter = spotifyAdapter
        }

        viewModel.getSpotifyData { results -> spotifyAdapter.spotifyItemsList = results }
    }

    companion object{
        fun startActivity(context: Context, searchQuery: String, token: String){
            val intent = Intent(context, SpotifyResultActivity::class.java)
            intent.putExtra(spotifyQueryExtraName, searchQuery)
            intent.putExtra(TOKEN_EXTRA_NAME, token)
            context.startActivity(intent)
        }
    }

}
