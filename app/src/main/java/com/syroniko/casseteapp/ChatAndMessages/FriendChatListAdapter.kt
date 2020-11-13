package com.syroniko.casseteapp.ChatAndMessages

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.ChatDB
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class FriendChatListAdapter @Inject constructor(
    @ActivityContext private val context: Context
) : RecyclerView.Adapter<FriendChatListAdapter.ViewHolder>() {

    var displayedChats: MutableList<DisplayedChat> = mutableListOf()
        set(value) {
            field = value
            field.sortByDescending { displayedChat ->
                displayedChat.timestamp
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_list_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = displayedChats[position].userName

        val lastMessageText = displayedChats[position].lastMessageText.replace("\n", " ")

        holder.lastMessageTextView.text = if (lastMessageText.length > 20){
            (lastMessageText.subSequence(0, 20).toString()) + "..."
        }
        else {
            lastMessageText
        }

        if (displayedChats[position].status == "online") {
            holder.isAvailableGreenCircle.visibility = View.VISIBLE
        }
        if (displayedChats[position].status == "offline") {
            holder.isAvailableGreenCircle.visibility = View.INVISIBLE
        }

        var style = if (displayedChats[position].lastMessageRead || displayedChats[position].lastMessageSentByMe){
            Typeface.NORMAL
        }
        else {
            Typeface.BOLD
        }

        holder.userName.setTypeface(null, style)
        holder.lastMessageTextView.setTypeface(null, style)

        holder.friendListRelativeLayout.setOnClickListener {
            style = Typeface.NORMAL
            holder.userName.setTypeface(null, style)
            holder.lastMessageTextView.setTypeface(null, style)
            
            ChatActivity.startActivity(context, displayedChats[position])
        }

        holder.friendListRelativeLayout.setOnLongClickListener {
            if (displayedChats[position].lastMessageSentByMe){
                context.toast("You cannot unread your own message.")
                return@setOnLongClickListener true
            }

            ChatDB.getDocumentFromId(displayedChats[position].chatId).addOnSuccessListener { document ->
                val chat = document.toObject(Chat::class.java) ?: return@addOnSuccessListener
                chat.messages[chat.messages.lastIndex].read = false
                ChatDB.update(displayedChats[position].chatId, hashMapOf(Pair("messages", chat.messages)))
            }

            style = Typeface.BOLD
            holder.userName.setTypeface(null, style)
            holder.lastMessageTextView.setTypeface(null, style)

            return@setOnLongClickListener true
        }

    }

    override fun getItemCount() = displayedChats.size

    fun sortListFromSearch(s: String){
        displayedChats.sortBy { displayedChat ->
            if (displayedChat.userName.startsWith(s, true)){
                displayedChat.userName
            }
            else{
                "zzz"
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendListRelativeLayout: RelativeLayout = itemView.findViewById(R.id.friend_list_relative_layout)
        val userName: TextView = itemView.findViewById(R.id.username_friend_list_item_message_fragment_recycler)
        val userImage: ImageView = itemView.findViewById(R.id.message_fragment_recycler_item_user_image)
        val isAvailableGreenCircle: ImageView = itemView.findViewById(R.id.is_online_on_green_circle_image)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.last_message_text_view)
    }

}