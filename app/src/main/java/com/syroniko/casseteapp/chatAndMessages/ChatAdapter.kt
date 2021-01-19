
package com.syroniko.casseteapp.chatAndMessages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.chatAndMessages.entities.ImageMessage
import com.syroniko.casseteapp.chatAndMessages.entities.Message
import com.syroniko.casseteapp.chatAndMessages.entities.MessageType
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ChatAdapter @Inject constructor(
    @ApplicationContext val context: Context
) : RecyclerView.Adapter<ChatViewHolder>() {

    var messages: MutableList<Message> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val uid = Auth.getUid()
    lateinit var imageUrl: String

    private var maxWidth: Double = 0.0
    private var maxHeight: Double = 0.0

    init {
        val maxWidthAndHeight = getMaxWidthAndHeight(context)
        maxWidth = maxWidthAndHeight[0]
        maxHeight = maxWidthAndHeight[1]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view: View
        val type: MessageType

        when (viewType) {
            MESSAGE_TEXT_RIGHT -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_text_right, parent, false)
                type = MessageType.TEXT
            }
            MESSAGE_TEXT_LEFT -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_text_left, parent, false)
                type = MessageType.TEXT
            }
            MESSAGE_IMAGE_RIGHT -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_image_right, parent, false)
                type = MessageType.IMAGE
            }
            MESSAGE_IMAGE_LEFT -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_image_left, parent, false)
                type = MessageType.IMAGE
            }
            else -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_spotify_track, parent, false)
                type = MessageType.SPOTIFY_TRACK
            }
        }

        return ChatViewHolder(view, type)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        if (holder.messageType == MessageType.TEXT) {
            holder.bindTextMessage(context, messages[position] as TextMessage)
        }
        else if (holder.messageType == MessageType.IMAGE){
            holder.bindImageMessage(context, messages[position] as ImageMessage, maxWidth, maxHeight)
        }
        else if (holder.messageType == MessageType.SPOTIFY_TRACK){
            //do nothing
        }

        if (position < messages.size - 1){
            if (messages[position].senderId != messages[position+1].senderId){
                addImage(context, messages[position].senderId, holder.userImage)
                holder.userImage.visibility = View.VISIBLE
            }
            else{
                if (messages[position].senderId != uid){
                    holder.userImage.visibility = View.INVISIBLE
                }
            }
        }
        else{
            addImage(context, messages[position].senderId, holder.userImage)
            holder.userImage.visibility = View.VISIBLE
        }

        if (messages[position].senderId == uid){
            holder.userImage.visibility = View.GONE
        }

    }

    override fun getItemCount() = messages.size

//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val showMessage: TextView = itemView.findViewById(R.id.chat_item_textView_bubble)
//        val userImage: ImageView = itemView.findViewById(R.id.profile_image_chat_activity)
//        val messageSeen: TextView = itemView.findViewById(R.id.message_seen_text)
//        val timestampTv: TextView = itemView.findViewById(R.id.timestamptextviewchat)
//    }

    override fun getItemViewType(position: Int): Int {
        val uid = Auth.getUid()
        val message = messages[position]
        return if((message is TextMessage) && message.senderId == uid){
            Log.d(ChatAdapter::class.simpleName, "RIGHT")
            MESSAGE_TEXT_RIGHT;
        }
        else if((message is TextMessage) && message.senderId != uid){
            Log.d(ChatAdapter::class.simpleName, uid ?: "LEFT")
            MESSAGE_TEXT_LEFT;
        }
        else if(message is ImageMessage && message.senderId == uid){
            MESSAGE_IMAGE_RIGHT
        }
        else if(message is ImageMessage && message.senderId != uid){
            MESSAGE_IMAGE_LEFT
        }
        else {
            MESSAGE_SPOTIFY_TRACK
        }
    }



    companion object {
        const val MESSAGE_TEXT_RIGHT = 0
        const val MESSAGE_TEXT_LEFT = 1
        const val MESSAGE_IMAGE_RIGHT = 2
        const val MESSAGE_IMAGE_LEFT = 3
        const val MESSAGE_SPOTIFY_TRACK = 4
    }

}