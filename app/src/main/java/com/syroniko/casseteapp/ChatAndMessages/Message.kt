package com.syroniko.casseteapp.ChatAndMessages

class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = 0,
    var read: Boolean = false,
    val messageId: String = ""
)