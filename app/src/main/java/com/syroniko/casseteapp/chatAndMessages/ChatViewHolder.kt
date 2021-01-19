package com.syroniko.casseteapp.chatAndMessages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.chatAndMessages.entities.ImageMessage
import com.syroniko.casseteapp.chatAndMessages.entities.MessageType
import com.syroniko.casseteapp.chatAndMessages.entities.SpotifyTrackMessage
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.utils.addImageAndResize
import com.syroniko.casseteapp.utils.calculateParams
import java.text.SimpleDateFormat
import java.util.*

class ChatViewHolder(itemView: View, val messageType: MessageType) : RecyclerView.ViewHolder(itemView) {

    lateinit var showMessage: TextView
    val userImage: ImageView = itemView.findViewById(R.id.profile_image_chat_activity)
    lateinit var messageSeen: TextView
    lateinit var timestampTv: TextView
    lateinit var messageImageView: ImageView

    init {
        when (messageType) {
            MessageType.TEXT -> {
                showMessage = itemView.findViewById(R.id.chat_item_textView_bubble)
                messageSeen = itemView.findViewById(R.id.message_seen_text)
                timestampTv = itemView.findViewById(R.id.timestamptextviewchat)
            }
            MessageType.IMAGE -> {
                messageImageView = itemView.findViewById(R.id.chat_item_image_view)
            }
            MessageType.SPOTIFY_TRACK -> {

            }
        }
    }

    fun bindTextMessage(context: Context, message: TextMessage){
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

    fun bindImageMessage(context: Context, message: ImageMessage, maxWidth: Double, maxHeight: Double){
        messageImageView.visibility = View.INVISIBLE

        val imageRef = Firebase.storage.reference.child(message.imageRef)
        imageRef.metadata.addOnSuccessListener { metadata ->
            val width = metadata.getCustomMetadata("width")?.toInt()
            val height = metadata.getCustomMetadata("height")?.toInt()
            if (width == null || height == null){
                return@addOnSuccessListener
            }

            val params = calculateParams(context, width, height, maxWidth, maxHeight)

            addImageAndResize(
                context,
                message.imageRef,
                messageImageView,
                params[0], params[1],
                cropCircle = false,
                roundCorners = true
            )

            messageImageView.visibility = View.VISIBLE
        }
    }

    fun onBindSpotifyTrack(message: SpotifyTrackMessage){

    }
}
