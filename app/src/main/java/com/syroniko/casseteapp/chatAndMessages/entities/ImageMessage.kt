package com.syroniko.casseteapp.chatAndMessages.entities

class ImageMessage(
    messageId: String = "",
    senderId: String = "",
    receiverId: String = "",
    timestamp: Long = 0,
    read: Boolean = false,
    val imageRef: String = ""
) : Message(messageId, senderId, receiverId, timestamp, read, MessageType.IMAGE)