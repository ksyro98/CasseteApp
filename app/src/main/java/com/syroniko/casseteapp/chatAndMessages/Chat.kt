package com.syroniko.casseteapp.chatAndMessages

import com.syroniko.casseteapp.chatAndMessages.entities.Message
import com.syroniko.casseteapp.chatAndMessages.entities.TextMessage

class Chat(
    val uids: ArrayList<String> = arrayListOf(),
    val messages: ArrayList<Message> = arrayListOf(),
    var lastMessageSentAt: Long = 0,
    var id: String = "",
    var lastMessageSent: Message = TextMessage()
)

