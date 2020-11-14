package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.syroniko.casseteapp.MainClasses.*
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.SpotifyClasses.GENRES
import com.syroniko.casseteapp.SpotifyClasses.mapGenres
import com.syroniko.casseteapp.firebase.Auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_send_track.*

const val SPOTIFY_TRACK_RESULT_EXTRA_NAME = "Spotify Track Result Extra Name"

@AndroidEntryPoint
class SendTrackActivity : AppCompatActivity() {

    private val viewModel by viewModels<SendTrackViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_track)

        if(intent == null){
            return
        }

        viewModel.track = intent.getParcelableExtra(SPOTIFY_TRACK_RESULT_EXTRA_NAME) ?: return
        viewModel.user = intent.getParcelableExtra(USER_EXTRA_NAME) ?: return

        viewModel.getGenre(::updateGenreTextView)

        Glide.with(this).load(viewModel.track.imageUrl).into(trackImageView)
        trackTitleTextView.text = viewModel.track.trackName
        artistNameTextView.text = viewModel.track.artistNames.toString()

        if (viewModel.track.previewUrl == NO_PREVIEW_URL){
            longToast("This track has no available preview, so only people who use Spotify premium will be able to listen to it.")
        }

        changeGenreTextView.setOnClickListener {
            changeGenre(this)
        }

        sendButton.setOnClickListener {
            send()
        }

    }

    private fun updateGenreTextView(genre: String?){
        val updatedGenre = mapGenres(genre)
        genreTextView.text = updatedGenre
    }

    private fun changeGenre(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick a genre")
        builder.setItems(GENRES) { dialog, which ->
            viewModel.track.genre = GENRES[which]
            genreTextView.text = GENRES[which]
            dialog.dismiss()
        }
        builder.show()
    }

    private fun send(){
        if (viewModel.track.genre == null){
            toast("Please select a genre for this track.")
            return
        }
        sendButton.isEnabled = false

        viewModel.sendCassette(commentEditText.text.toString()){
            toast("Cassette sent successfully!")

            sendButton.isEnabled = true

            MainActivity.startActivity(this, Auth.getUid(), viewModel.user)
            finish()
        }
    }


    companion object {
        fun startActivity(context: Context, track: SpotifyTrack, user: User){
            val trackIntent = Intent(context, SendTrackActivity::class.java)
            trackIntent.putExtra(SPOTIFY_TRACK_RESULT_EXTRA_NAME, track)
            trackIntent.putExtra(USER_EXTRA_NAME, user)
            trackIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(trackIntent)
        }
    }

}
