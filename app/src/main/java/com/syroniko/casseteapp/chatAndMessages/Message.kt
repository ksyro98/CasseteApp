package com.syroniko.casseteapp.chatAndMessages

class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = 0,
    var read: Boolean = false,
    val messageId: String = ""
)