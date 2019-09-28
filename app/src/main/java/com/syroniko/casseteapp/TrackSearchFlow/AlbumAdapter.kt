package com.syroniko.casseteapp.TrackSearchFlow

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syroniko.casseteapp.MainClasses.tokenExtraName
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
import kotlinx.android.synthetic.main.spotify_item_album.view.*

const val spotifyAlbumIdExtraName = "Spotify Album Id Extra Name"
const val spotifyAlbumImageUrlExtraName = "Spotify Album Image Url Extra Name"

class AlbumAdapter(private val context: Context, private val albumList: ArrayList<SpotifyAlbum>, private val token: String) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.spotify_item_album
        val layoutInflater = LayoutInflater.from(context)

        val view = layoutInflater.inflate(layoutIdForListItem, parent, false)

        return AlbumViewHolder(view)
    }

    override fun getItemCount(): Int {
//        context.toast(albumList.size.toString())
        return albumList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        Glide.with(context).clear(holder.itemView.albumImageView)
        Glide.with(context).load(albumList[position].imageUrl).into(holder.itemView.albumImageView)

        holder.itemView.albumTitleTextView.text = albumList[position].albumName

        holder.itemView.setOnClickListener {
            val albumId = albumList[position].albumId
            val albumImageUrl = albumList[position].imageUrl

            val trackIntent = Intent(context, SpotifyAlbumResultActivity::class.java)

            trackIntent.putExtra(spotifyAlbumIdExtraName, albumId)
            trackIntent.putExtra(spotifyAlbumImageUrlExtraName, albumImageUrl)
            trackIntent.putExtra(tokenExtraName, token)

            context.startActivity(trackIntent)
        }
    }


    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}