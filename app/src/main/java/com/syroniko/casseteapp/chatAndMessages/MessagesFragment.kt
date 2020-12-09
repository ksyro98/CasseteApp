package com.syroniko.casseteapp.chatAndMessages

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.mainClasses.MainViewModel
import com.syroniko.casseteapp.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()
    @Inject lateinit var chatsAdapter: FriendChatListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_of_friends_message_fragment)
        val searchEditText: EditText = view.findViewById(R.id.search_users_edit_text)

        if (viewModel.chats.value != null) {
            chatsAdapter.displayedChats = viewModel.chats.value!!
        }

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }

        viewModel.chats.observe(viewLifecycleOwner, Observer { displayedChatsList ->
//            old code to update the chat, keep it here until we ensure that the new one works
//            chatsAdapter.displayedChats = displayedChatsList ?: return@Observer

            //we use "<" instead of "!=" in order to be able to work in cases where a new contact or
            //a very old contact (not loaded without scroll down) send a message and there are more
            //documents in the displayedChatList
            //IMP: currently this isn't implemented, but it's written like that to make it in the future
            if (displayedChatsList.size < chatsAdapter.displayedChats.size){
                return@Observer
            }

            //used to smoothly update the positions of the items in the adapter (see below)
            val positionsMap = mutableMapOf<Int, String>()

            //for each new chat and each chat in the adapter check if they have the same chatId but
            //they are different (this means that this chat has been updated
            //if yes, check if they have different timestamps (this means that a new message was
            //sent, if they do update the positions map
            //finally change the item in the adapter and notify the adapter
            //important: if we use notifyDataSetChange the update animation is very bad
            displayedChatsList.map { displayedChat ->
                for (i in 0 until chatsAdapter.displayedChats.size){
                    val adapterChatI = chatsAdapter.displayedChats[i]

                    if (displayedChat.chatId == adapterChatI.chatId && displayedChat != adapterChatI){
                        if (displayedChat.timestamp != adapterChatI.timestamp) {
                            //we temporarily set the chatId as value to be able to identify the
                            //chat later (that's also why we use String as the value's type)
                            //however the value of the map is not intended for this
                            positionsMap[i] = adapterChatI.chatId
                        }
                        chatsAdapter.displayedChats[i] = displayedChat
                        chatsAdapter.notifyItemChanged(i)
                    }
                }
            }

            //sort the new chats (newest goes first)
            chatsAdapter.displayedChats.sortByDescending { it.timestamp }

            //for each of the newly updated chats (in the adapter) check if their chatId exists as
            //value of the map
            //if yes, replace that value with the current position of the chat, this way the map
            //will show the change of the positions like that:
            //key = oldPosition -> value = newPosition
            var i = 0
            chatsAdapter.displayedChats.map { chat ->
                positionsMap.map { entry ->
                    if (chat.chatId == entry.value){
                        positionsMap[entry.key] = i.toString()
                        i++
                    }
                }
            }

            //for every entry of the positions map notify the adapter that the item is moved
            //from the old position (key) to the new position (value)
            //once again if we just use notifyDataSetChange the update animation will be a problem
            positionsMap.map { entry ->
                chatsAdapter.notifyItemMoved(entry.key, entry.value.toInt())
            }

        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                chatsAdapter.sortListFromSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable) {}
        })

        return view
    }

}