package com.syroniko.casseteapp.chatAndMessages

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.ChatDB
import com.syroniko.casseteapp.utils.addAndUpdate
import javax.inject.Inject

class ChatViewModel  @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    val uid = Auth.getUid() ?: ""
    lateinit var displayedChat: DisplayedChat
    val messages: MutableLiveData<MutableList<Message>> by lazy {
        MutableLiveData<MutableList<Message>>()
    }
    var fromNotification = false

    fun startListeningToMessages(){
        ChatDB.listenToMessages(displayedChat.chatId) { document ->

            messages.value = mutableListOf()

           val chat = document.toObject(Chat::class.java) ?: return@listenToMessages

            chat.messages.map { message ->
                if(message.senderId != uid) message.read = true
                messages.addAndUpdate(message)
            }

            ChatDB.update(displayedChat.chatId, hashMapOf(Pair("messages", chat.messages)))
        }
    }

    fun stopListeningToMessages(){
        ChatDB.detachListener()
    }
}