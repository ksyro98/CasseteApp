package com.syroniko.casseteapp.chatAndMessages.entities

enum class MessageType{
    TEXT,
    IMAGE,
    SPOTIFY_TRACK;

    companion object {
        fun getFromString(stringType: String): MessageType? {
            return when(stringType){
                "TEXT" -> TEXT
                "IMAGE" -> IMAGE
                "SPOTIFY_TRACK" -> SPOTIFY_TRACK
                else -> null
            }
        }
    }

    fun getMessageClass(): Class<*>{
        return when(this) {
            TEXT -> TextMessage::class.java
            IMAGE -> ImageMessage::class.java
            SPOTIFY_TRACK -> SpotifyTrackMessage::class.java
        }
    }

    fun getMessageText(sentByUser: Boolean, senderName: String): String?{
        return when(this) {
            TEXT -> null
            IMAGE -> if (!sentByUser) "$senderName sent an image." else "You sent an image."
            SPOTIFY_TRACK -> if (!sentByUser) "$senderName name sent a track." else "You name sent a track."
        }
    }
}

abstract class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0,
    var read: Boolean = false,
    val type: MessageType = MessageType.TEXT
){
    companion object {
        fun getMessageId(chatId: String, timestamp: Long): String{
            return chatId + timestamp.toString()
        }
    }
}