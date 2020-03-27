package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.syroniko.casseteapp.MainClasses.spotifyArtistResultExtraName
import com.syroniko.casseteapp.MainClasses.spotifyTrackResultExtraName
import com.syroniko.casseteapp.MainClasses.tokenExtraName
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.*
import kotlinx.android.synthetic.main.spotify_item.view.*

class SpotifyAdapter(private val activity: AppCompatActivity, private val spotifyItemsList: ArrayList<SpotifyResult>, private var token: String?) : RecyclerView.Adapter<SpotifyAdapter.SpotifyViewHolder>() {


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
        holder.itemView.spotifyImageView.layoutParams = LinearLayout.LayoutParams((64 * activity.resources.displayMetrics.density).toInt(), (64 * activity.resources.displayMetrics.density).toInt())
//        holder.itemView.spotifyImageView
        Glide.with(activity).clear(holder.itemView.spotifyImageView)

        when (spotifyItemsList[position].getClass()){
            track -> {
                val track = spotifyItemsList[position] as SpotifyTrack
                holder.itemView.spotifyTextView.text = track.trackName
                for(i in track.artistNames.indices) {
                    holder.itemView.spotifyDetailsTextView.append(track.artistNames[i])
                    if(i != track.artistNames.size-1){
                        holder.itemView.spotifyDetailsTextView.append(", ")
                    }
                }
                Glide.with(activity).load(track.imageUrl).into(holder.itemView.spotifyImageView)
                holder.itemView.setOnClickListener {
                    val trackIntent = Intent(activity, SendTrackActivity::class.java)
                    trackIntent.putExtra(spotifyTrackResultExtraName, track)
                    trackIntent.putExtra(tokenExtraName, token)
                    trackIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    activity.startActivity(trackIntent)
                }
            }

            artist -> {
                val artist = spotifyItemsList[position] as SpotifyArtist
                holder.itemView.spotifyTextView.text = artist.artistName
                holder.itemView.spotifyDetailsTextView.text = activity.getString(R.string.artist)
                Glide.with(activity).load(artist.imageUrl).apply(RequestOptions.circleCropTransform()).into(holder.itemView.spotifyImageView)
                holder.itemView.setOnClickListener {
                    val artistIntent = Intent(activity, SpotifyArtistResultActivity::class.java)
                    artistIntent.putExtra(spotifyArtistResultExtraName, artist.artistName)
                    artistIntent.putExtra(tokenExtraName, token)
                    activity.startActivity(artistIntent)
                }
            }

            separator -> {
                holder.itemView.spotifyTextView.text = "\t"
                holder.itemView.spotifyTextView.append((spotifyItemsList[position] as SpotifySeparator).message)
                holder.itemView.spotifyDetailsTextView.maxLines = 0
                holder.itemView.spotifyImageView.layoutParams = LinearLayout.LayoutParams(0, 0)
            }
        }
    }

    fun setToken(token: String){
        this.token = token
    }

    class SpotifyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}