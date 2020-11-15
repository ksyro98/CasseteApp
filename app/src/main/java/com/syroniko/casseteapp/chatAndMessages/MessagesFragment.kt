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
                chatsAdapter.displayedChats = displayedChatsList ?: return@Observer
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