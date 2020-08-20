package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.MainClasses.*
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import com.syroniko.casseteapp.SpotifyClasses.genres
import kotlinx.android.synthetic.main.activity_send_track.*


class SendTrackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_track)

        if(intent == null){
            return
        }

        val track = intent.getParcelableExtra<SpotifyTrack>(spotifyTrackResultExtraName)
        val token = intent.getStringExtra(tokenExtraName)
        val context = this

        if(track == null || token == null){
            return
        }

        track.getGenreFromArtists(Volley.newRequestQueue(this), token, ::updateGenreTextView)

        Glide.with(this).load(track.imageUrl).into(trackImageView)
        trackTitleTextView.text = track.trackName
        artistNameTextView.text = track.artistNames.toString()

        if (track.previewUrl == noPreviewUrl){
            longToast("This track has no available preview, so only to people who use Spotify premium will be able to listen to it.")
        }

        changeGenreTextView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Pick a genre")
            builder.setItems(genres) { dialog, which ->
                track.genre = genres[which]
                genreTextView.text = genres[which]
                dialog.dismiss()
            }
            builder.show()
        }



        sendButton.setOnClickListener {
            send(track)
        }

//        val cassette = Cassette(
//            uid,
//            track,
//            commentEditText.text.toString(),
//            genre,
//            possibleRecievers,
//            arrayListOf(uid))
    }

    private fun updateGenreTextView(genre: String?){
        val updatedGenre = mapGenres(genre)
        genreTextView.text = updatedGenre
    }

    private fun send(track: SpotifyTrack){
        sendButton.isEnabled = false

        val uid = FirebaseAuth.getInstance().uid
        val genre = genreTextView.text.toString()
        val possibleReceivers = arrayListOf<String>()
        val restrictedReceivers = arrayListOf(uid)
        val db = FirebaseFirestore.getInstance()


        db.collection("users")
            .orderBy("receivedLastCassetteAt")
            .whereArrayContains("genres", genre)
            .limit(10)
            .get()
            .addOnSuccessListener {documents ->
                for (document in documents){
                    val userId = document.data["uid"].toString()
                    if (!restrictedReceivers.contains(userId)){
                        possibleReceivers.add(userId)
                    }
                }

                val cassette = Cassette(
                    uid,
                    track,
                    commentEditText.text.toString(),
                    genre,
                    possibleReceivers,
                    restrictedReceivers)

                db.collection("cassettes").add(cassette)

                toast("Cassette sent succesfully!")

                sendButton.isEnabled = true


                val sendIntent = Intent(this, MainActivity::class.java)
                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                startActivity(sendIntent)
            }
            .addOnFailureListener {
                Log.e(SendTrackActivity::class.java.simpleName, "Retrieving Data Error", it)
            }
    }

    private fun mapGenres(genre: String?) : String?{
        return when(genre){
            "blues" -> "Blues"
            "classical" -> "Classical"
            "country" -> "Country"
            "electronic" -> "Electronic"
            "folk" -> "Folk"
            "hip-hop" -> "Hip-Hop"
            "jazz" -> "Jazz"
            "metal" -> "Metal"
            "pop" -> "Pop"
            "punk" -> "Punk"
            "r&b" -> "R&B"
            "rock" -> "Rock"
            "soundtracks" -> "Soundtracks"
            else -> genre
        }
    }

}
