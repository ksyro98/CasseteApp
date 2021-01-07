package com.syroniko.casseteapp.chatAndMessages.entities

enum class MessageType{
    TEXT,
    IMAGE,
    SPOTIFY_TRACK
}

abstract class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0,
    var read: Boolean = false,
    val type: MessageType
){
    companion object {
        fun getMessageId(chatId: String, timestamp: Long): String{
            return chatId + timestamp.toString()
        }
    }
}