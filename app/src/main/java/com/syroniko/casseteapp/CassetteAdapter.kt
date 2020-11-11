package com.syroniko.casseteapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView                    //todo add cassette to recycler view
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.MainClasses.Cassette
import com.syroniko.casseteapp.MainClasses.CASSETTE_VIEWER_REQUEST_CODE
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

    class CassetteViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CassetteViewHolder {
        val cassetteTextView = LayoutInflater.from(parent.context).inflate(R.layout.cassette_item, parent, false) as TextView

        return CassetteViewHolder(cassetteTextView)
    }

    override fun onBindViewHolder(holder: CassetteViewHolder, position: Int) {
        holder.textView.text = cassettes[position].track.trackName

        holder.textView.setOnClickListener {
            val intent = Intent(context, CassetteViewerActivity::class.java)
            intent.putExtra(cassetteIdExtraName, cassettes[position].getId())
            intent.putExtra(userIdExtraName, cassettes[position].senderId)

            (context as AppCompatActivity).startActivityForResult(intent, CASSETTE_VIEWER_REQUEST_CODE)
        }
    }

    override fun getItemCount() = cassettes.size
}