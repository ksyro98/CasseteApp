package com.syroniko.casseteapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.spotify_item_album.view.*


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
    }


    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}