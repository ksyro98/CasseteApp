package com.syroniko.casseteapp.chatAndMessages

class Chat(
    val uids: ArrayList<String> = arrayListOf(),
    val messages: ArrayList<Message> = arrayListOf(),
    var lastMessageSent: Long = 0,
    var id: String = "")

