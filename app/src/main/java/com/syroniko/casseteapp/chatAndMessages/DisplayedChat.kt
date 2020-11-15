package com.syroniko.casseteapp.chatAndMessages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DisplayedChat(
    val userId: String = "",
    val userImage: String = "",
    val userName: String = "",
    val status: String = "",
    val lastMessageText: String = "",
    val lastMessageRead: Boolean = false,
    val lastMessageSentByMe: Boolean = false,
    val timestamp: Long = 0,
    val chatId: String = ""
): Parcelable