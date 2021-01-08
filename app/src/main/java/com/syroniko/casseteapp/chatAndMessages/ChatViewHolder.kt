package com.syroniko.casseteapp.chatAndMessages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.chatAndMessages.entities.ImageMessage
import com.syroniko.casseteapp.chatAndMessages.entities.SpotifyTrackMessage
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage
import com.syroniko.casseteapp.mainClasses.toast
import java.text.SimpleDateFormat
import java.util.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val showMessage: TextView = itemView.findViewById(R.id.chat_item_textView_bubble)
    val userImage: ImageView = itemView.findViewById(R.id.profile_image_chat_activity)
    val messageSeen: TextView = itemView.findViewById(R.id.message_seen_text)
    val timestampTv: TextView = itemView.findViewById(R.id.timestamptextviewchat)

    fun onBindText(context: Context, message: TextMessage){
        showMessage.text = message.text
        timestampTv.visibility = View.GONE
        Glide.with(context).load("").into(userImage)

        val sdf = SimpleDateFormat("hh:mm dd/MM/yyyy")
        val netDate = Date(message.timestamp)
        timestampTv.text = sdf.format(netDate)

        messageSeen.text = if (message.read){
            "Seen"
        }
        else{
            "Sent"
        }

        showMessage.setOnClickListener {
            if (timestampTv.visibility == View.GONE) {
                timestampTv.visibility = View.VISIBLE
                messageSeen.visibility = View.VISIBLE
            }
            else {
                timestampTv.visibility = View.GONE
                messageSeen.visibility = View.GONE
            }
        }

        showMessage.setOnLongClickListener {
            val clipboard: ClipboardManager? = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Selected Message", showMessage.text.toString())
            clipboard?.setPrimaryClip(clip)
            context.toast("Message copied to clipboard.")
            return@setOnLongClickListener true
        }


    }

    fun onBindImage(message: ImageMessage){

    }

    fun onBindSpotifyTrack(message: SpotifyTrackMessage){

    }
}
