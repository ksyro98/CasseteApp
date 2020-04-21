package com.syroniko.casseteapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView                    //todo add cassette to recycler view
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.MainClasses.cassetteViewerRequestCode
import com.syroniko.casseteapp.room.LocalCassette
import com.syroniko.casseteapp.viewCassette.CassetteViewerActivity


const val cassetteIdExtraName = "Cassette Id Extra Name"
const val userIdExtraName = "User Id Extra Name"


class CassetteAdapter(private val context: Context, private val cassettes: MutableList<LocalCassette>) :
        RecyclerView.Adapter<CassetteAdapter.CassetteViewHolder>(){


    class CassetteViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CassetteViewHolder {
        val cassetteTextView = LayoutInflater.from(parent.context).inflate(R.layout.cassette_item, parent, false) as TextView

        return CassetteViewHolder(cassetteTextView)
    }

    override fun onBindViewHolder(holder: CassetteViewHolder, position: Int) {
        holder.textView.text = cassettes[position].trackName

        holder.textView.setOnClickListener {
            val intent = Intent(context, CassetteViewerActivity::class.java)
            intent.putExtra(cassetteIdExtraName, cassettes[position].cassetteId)
            intent.putExtra(userIdExtraName, cassettes[position].senderId)
//            context.startActivity(intent)

            (context as AppCompatActivity).startActivityForResult(intent, cassetteViewerRequestCode)
        }
    }

    override fun getItemCount() = cassettes.size
}