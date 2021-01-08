package com.syroniko.casseteapp.chatAndMessages

import android.os.Parcelable
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DisplayedChat(
    val userId: String = "",    //this is not the user id of the current user, it's the user id of the user he's talking with
    val userImage: String = "",
    val userName: String = "",
    val status: String = "",
    val lastMessageText: String = "",
    val lastMessageRead: Boolean = false,
    val lastMessageSentByMe: Boolean = false,
    val timestamp: Long = 0,
    val chatId: String = ""
): Parcelable

