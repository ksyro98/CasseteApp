package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.spotify_item_album.view.*
import javax.inject.Inject

const val spotifyAlbumIdExtraName = "Spotify Album Id Extra Name"
const val spotifyAlbumImageUrlExtraName = "Spotify Album Image Url Extra Name"

class AlbumAdapter @Inject constructor(
    @ActivityContext private val context: Context
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    var albumList = arrayListOf<SpotifyAlbum>()
        set(value){
            field = value
            this.notifyDataSetChanged()
        }
    lateinit var user: User

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.spotify_item_album, parent, false)

        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        Glide.with(context).clear(holder.itemView.albumImageView)
        Glide.with(context).load(albumList[position].imageUrl).into(holder.itemView.albumImageView)

        holder.itemView.albumTitleTextView.text = albumList[position].albumName

        holder.itemView.setOnClickListener {
            val albumId = albumList[position].albumId
            val albumImageUrl = albumList[position].imageUrl

            SpotifyAlbumResultActivity.startActivity(context, albumId, albumImageUrl, user)
        }
    }

    override fun getItemCount() = albumList.size


    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}