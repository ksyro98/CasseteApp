package com.syroniko.casseteapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syroniko.casseteapp.mainClasses.Cassette
import com.syroniko.casseteapp.utils.addImage
import com.syroniko.casseteapp.viewCassette.CASSETTE_VIEWER_REQUEST_CODE
import com.syroniko.casseteapp.viewCassette.CassetteViewerActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


const val cassetteIdExtraName = "Cassette Id Extra Name"
const val userIdExtraName = "User Id Extra Name"


class CassetteAdapter @Inject constructor(@ActivityContext private val context: Context) :
        RecyclerView.Adapter<CassetteAdapter.CassetteViewHolder>(){

    var cassettes: MutableList<Cassette> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class CassetteViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val songNametextView: TextView = view.findViewById(R.id.profile_song_name_textview_recent_cassettes_main_recycler)
        val artistNameTextView: TextView = view.findViewById(R.id.profile_song_artist_textview_recent_cassettes_main_recycler)
        val noteTextView: TextView = view.findViewById(R.id.profile_cassette_note_textview_recent_cassettes_main_recycler)
        val profileimageView: ImageView = view.findViewById(R.id.profile_image_recent_cassettes_main_recycler)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CassetteViewHolder {
        val cassetteTextView = LayoutInflater.from(parent.context).inflate(R.layout.cassette_item, parent, false)

        return CassetteViewHolder(cassetteTextView)
    }

    override fun onBindViewHolder(holder: CassetteViewHolder, position: Int) {
        holder.songNametextView.text = cassettes[position].track.trackName
        holder.artistNameTextView.text = cassettes[position].track.artistNames[0]
        var stringBuilderName = StringBuilder(cassettes[position].comment)
        if (stringBuilderName.length > 84) {
            stringBuilderName.setCharAt(82, '.')
            stringBuilderName.setCharAt(83, '.')
            stringBuilderName.setCharAt(81, '.')
            stringBuilderName.delete(84, stringBuilderName.length)
        }
            holder.noteTextView.text = stringBuilderName.toString()
            Glide.with(context).load(cassettes[position].track.imageUrl)
                .into(holder.profileimageView)
        holder.profileimageView.setOnClickListener {
            val intent = Intent(context, CassetteViewerActivity::class.java)
            intent.putExtra(cassetteIdExtraName, cassettes[position].id)
            intent.putExtra(userIdExtraName, cassettes[position].senderId)

            (context as AppCompatActivity).startActivityForResult(intent, CASSETTE_VIEWER_REQUEST_CODE)
        }
        }


    override fun getItemCount() = cassettes.size
}
