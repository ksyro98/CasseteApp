package com.syroniko.casseteapp.ChatAndMessages

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.ChatDB
import com.syroniko.casseteapp.firebase.UserDB
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MessagesFragment : Fragment() {

    @Inject lateinit var chatsAdapter: FriendChatListAdapter
    private val uid: String? = Auth.getUid()

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    private val chatDB = ChatDB()
    private val userDB = UserDB()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        recyclerView = view.findViewById(R.id.recycler_of_friends_message_fragment)
        searchEditText = view.findViewById(R.id.search_users_edit_text)

        if (uid == null) {
            return view
        }

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                chatsAdapter.sortListFromSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })


        return view
    }

    override fun onStart() {
        super.onStart()

        if (uid == null){
            return
        }

        chatDB.getChatsThatIncludesUser(uid)
            .addOnSuccessListener { documentSnapshot ->
                val chats = arrayListOf<Chat>()
                documentSnapshot.map { document ->
                    chats.add(document.toObject(Chat::class.java))
                }

                val displayedChats = arrayListOf<DisplayedChat>()
                chats.map{ chat ->
                    val timestamp = chat.lastMessageSent
                    val lastMessageText = chat.messages.last().text
                    val lastMessageRead = chat.messages.last().read
                    val lastMessageSentByMe = chat.messages.last().senderId == uid
                    val chatId = chat.id

                    val otherUid = getTheOtherUid(chat.uids, uid) ?: return@addOnSuccessListener

                    userDB.getDocumentFromId(otherUid).addOnSuccessListener { document ->
                        val otherUser = document.toObject(User::class.java)
                        displayedChats.add(DisplayedChat(
                            otherUser?.uid ?: "",
                            otherUser?.image ?: "",
                            otherUser?.name ?: "",
                            otherUser?.status ?: "",
                            lastMessageText,
                            lastMessageRead,
                            lastMessageSentByMe,
                            timestamp,
                            chatId
                        ))

                        chatsAdapter.displayedChats = displayedChats
                    }
                }
            }
    }

}