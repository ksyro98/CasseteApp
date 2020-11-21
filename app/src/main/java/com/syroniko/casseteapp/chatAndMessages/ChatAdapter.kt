package com.syroniko.casseteapp.chatAndMessages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.utils.addImage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ChatAdapter @Inject constructor(
    @ApplicationContext val context: Context
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var messages: MutableList<Message> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var imageUrl: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGE_RIGHT) {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showMessage.text = messages[position].text
        holder.timestampTv.visibility = View.INVISIBLE

        val sdf = SimpleDateFormat("hh:mm dd/MM/yyyy")
        val netDate = Date(messages[position].timestamp)
        holder.timestampTv.text = sdf.format(netDate)

        holder.messageSeen.text = if (messages[position].read){
            "Seen"
        }
        else{
            "Sent"
        }

        holder.showMessage.setOnClickListener {
            if (holder.timestampTv.visibility == View.INVISIBLE) {
                holder.timestampTv.visibility = View.VISIBLE
                holder.messageSeen.visibility = View.VISIBLE
            }
            else {
                holder.timestampTv.visibility = View.INVISIBLE
                holder.messageSeen.visibility = View.INVISIBLE
            }
        }

        holder.showMessage.setOnLongClickListener {
            val clipboard: ClipboardManager? = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Selected Message", holder.showMessage.text.toString())
            clipboard?.setPrimaryClip(clip)
            context.toast("Message copied to clipboard.")
            return@setOnLongClickListener true
        }

        addImage(context, messages[position].senderId, holder.userImage)
    }

    override fun getItemCount() = messages.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val showMessage: TextView = itemView.findViewById(R.id.chat_item_textView_bubble)
        val userImage: ImageView = itemView.findViewById(R.id.profile_image_chat_activity)
        val messageSeen: TextView = itemView.findViewById(R.id.message_seen_text)
        val timestampTv: TextView = itemView.findViewById(R.id.timestamptextviewchat)
//        val messageSeenTv: TextView = itemView.findViewById(R.id.message_read_text_view)

    }

    override fun getItemViewType(position: Int): Int {
        val uid = Auth.getUid()
        return if(messages[position].senderId == uid){
            Log.d(ChatAdapter::class.simpleName, "RIGHT")
            MESSAGE_RIGHT;
        }
        else{
            Log.d(ChatAdapter::class.simpleName, uid ?: "LEFT")
            MESSAGE_LEFT;
        }
    }

    companion object {
        const val MESSAGE_RIGHT = 0
        const val MESSAGE_LEFT = 1
    }

}