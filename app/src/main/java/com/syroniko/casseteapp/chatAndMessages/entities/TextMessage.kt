package com.syroniko.casseteapp.chatAndMessages.entities

class TextMessage(
    messageId: String = "",
    senderId: String = "",
    receiverId: String = "",
    timestamp: Long = 0,
    read: Boolean = false,
    val text: String = ""
) : Message(messageId, senderId, receiverId, timestamp, read, MessageType.TEXT)