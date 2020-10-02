package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.syroniko.casseteapp.MainClasses.spotifyArtistResultExtraName
import com.syroniko.casseteapp.MainClasses.spotifyTrackResultExtraName
import com.syroniko.casseteapp.MainClasses.tokenExtraName
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.*
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
    var token: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotifyViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.spotify_item
        val layoutInflater = LayoutInflater.from(context)

        val view = layoutInflater.inflate(layoutIdForListItem, parent, false)

        return SpotifyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return spotifyItemsList.size
    }

    override fun onBindViewHolder(holder: SpotifyViewHolder, position: Int) {
        holder.itemView.spotifyTextView.text = ""
        holder.itemView.spotifyDetailsTextView.text = ""
        holder.itemView.spotifyDetailsTextView.maxLines = 1
        holder.itemView.spotifyImageView.layoutParams = LinearLayout.LayoutParams((64 * context.resources.displayMetrics.density).toInt(), (64 * context.resources.displayMetrics.density).toInt())
//        holder.itemView.spotifyImageView
        Glide.with(context).clear(holder.itemView.spotifyImageView)

        when (spotifyItemsList[position].getClass()){
            TRACK -> {
                val track = spotifyItemsList[position] as SpotifyTrack
                holder.itemView.spotifyTextView.text = track.trackName
                for(i in track.artistNames.indices) {
                    holder.itemView.spotifyDetailsTextView.append(track.artistNames[i])
                    if(i != track.artistNames.size-1){
                        holder.itemView.spotifyDetailsTextView.append(", ")
                    }
                }
                Glide.with(context).load(track.imageUrl).into(holder.itemView.spotifyImageView)
                holder.itemView.setOnClickListener {
                    val trackIntent = Intent(context, SendTrackActivity::class.java)
                    trackIntent.putExtra(spotifyTrackResultExtraName, track)
                    trackIntent.putExtra(tokenExtraName, token)
                    trackIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(trackIntent)
                }
            }

            ARTIST -> {
                val artist = spotifyItemsList[position] as SpotifyArtist
                holder.itemView.spotifyTextView.text = artist.artistName
                holder.itemView.spotifyDetailsTextView.text = context.getString(R.string.artist)
                Glide.with(context).load(artist.imageUrl).apply(RequestOptions.circleCropTransform()).into(holder.itemView.spotifyImageView)
                holder.itemView.setOnClickListener {
                    val artistIntent = Intent(context, SpotifyArtistResultActivity::class.java)
                    artistIntent.putExtra(spotifyArtistResultExtraName, artist.artistName)
                    artistIntent.putExtra(tokenExtraName, token)
                    context.startActivity(artistIntent)
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

    class SpotifyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}