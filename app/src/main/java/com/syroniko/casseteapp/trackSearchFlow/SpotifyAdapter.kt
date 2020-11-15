package com.syroniko.casseteapp.trackSearchFlow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.spotifyClasses.*
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.spotify_item.view.*
import javax.inject.Inject

class SpotifyAdapter @Inject constructor(
    @ActivityContext private val context: Context
) : RecyclerView.Adapter<SpotifyAdapter.SpotifyViewHolder>() {

    var spotifyItemsList = arrayListOf<SpotifyResult>()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }
    lateinit var user: User

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotifyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.spotify_item, parent, false)

        return SpotifyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpotifyViewHolder, position: Int) {
        resetItem(holder)

        when (spotifyItemsList[position].getSpotifyClass()){
            TRACK -> {
                val track = spotifyItemsList[position] as SpotifyTrack

                holder.itemView.spotifyTextView.text = track.trackName
                holder.itemView.spotifyDetailsTextView.text = generateArtistNamesString(track.artistNames)
                Glide.with(context).load(track.imageUrl).into(holder.itemView.spotifyImageView)

                holder.itemView.setOnClickListener {
                    SendTrackActivity.startActivity(context, track, user)
                }

            }

            ARTIST -> {
                val artist = spotifyItemsList[position] as SpotifyArtist

                holder.itemView.spotifyTextView.text = artist.artistName
                holder.itemView.spotifyDetailsTextView.text = context.getString(R.string.artist)
                Glide.with(context).load(artist.imageUrl).apply(RequestOptions.circleCropTransform()).into(holder.itemView.spotifyImageView)

                holder.itemView.setOnClickListener {
                    SpotifyArtistResultActivity.startActivity(context, artist.artistName, user)
                }

            }

            SEPARATOR -> {
                holder.itemView.spotifyTextView.text = "\t"
                holder.itemView.spotifyTextView.append((spotifyItemsList[position] as SpotifySeparator).message)
                holder.itemView.spotifyDetailsTextView.maxLines = 0
                holder.itemView.spotifyImageView.layoutParams = LinearLayout.LayoutParams(0, 0)
                holder.itemView.setOnClickListener {
                    //do nothing
                }
            }
        }
    }

    override fun getItemCount() = spotifyItemsList.size


    private fun resetItem(holder: SpotifyViewHolder){
        holder.itemView.spotifyTextView.text = ""
        holder.itemView.spotifyDetailsTextView.text = ""
        holder.itemView.spotifyDetailsTextView.maxLines = 1
        holder.itemView.spotifyImageView.layoutParams = LinearLayout.LayoutParams((64 * context.resources.displayMetrics.density).toInt(), (64 * context.resources.displayMetrics.density).toInt())
        Glide.with(context).clear(holder.itemView.spotifyImageView)

    }

    private fun generateArtistNamesString(artistNames: ArrayList<String>): String{
        var res = ""

        for(i in artistNames.indices) {
            res += artistNames[i]
            if(i != artistNames.size-1){
                res += ", "
            }
        }

        return res
    }

    class SpotifyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}