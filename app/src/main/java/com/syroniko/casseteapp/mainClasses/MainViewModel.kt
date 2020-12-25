package com.syroniko.casseteapp.mainClasses

import android.app.Application
import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.chatAndMessages.Chat
import com.syroniko.casseteapp.chatAndMessages.DisplayedChat
import com.syroniko.casseteapp.chatAndMessages.getTheOtherUid
import com.syroniko.casseteapp.chatAndMessages.getTime
import com.syroniko.casseteapp.firebase.*
import com.syroniko.casseteapp.utils.addAndUpdate
import javax.inject.Inject


class MainViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    var uid = Auth.getUid() ?: ""
    val cassettes: MutableLiveData<MutableList<Cassette>> by lazy {
        MutableLiveData<MutableList<Cassette>>()
    }
    val chats: MutableLiveData<MutableList<DisplayedChat>> by lazy {
        MutableLiveData<MutableList<DisplayedChat>>()
    }
    val friends: MutableLiveData<MutableList<User>> by lazy {
        MutableLiveData<MutableList<User>>()
    }
    var user: User = User()
//        set(value) {
//            field = value
//
//            getNewCassette()
//        }


    init {
        retrieveCassettes()
    }


    /**
     * MainActivity Functions
     */
    fun setUserOnline(){
        getTime(viewModelScope){ time ->
            UserDB.update(uid, hashMapOf(
                Pair("status", STATUS_ONLINE),
                Pair("lastOnline", time)
            ))
        }
    }

    fun setUserOffline(){
        UserDB.update(uid, hashMapOf(Pair("status", STATUS_OFFLINE)))
    }

    fun removeCassette(cassetteId: String){
        val tempValue = cassettes.value ?: return
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tempValue.removeIf {
                it.id == cassetteId
            }
        }
        else{
            for (cassette in tempValue){
                if (cassette.id == cassetteId){
                    tempValue.remove(cassette)
                }
            }
        }

        cassettes.value = tempValue
    }

    fun updateUserOnCassetteAction(senderId: String?, cassetteId: String?, userAccepted: Boolean){
        if(senderId != null) user.friends.add(senderId)

        if (cassetteId != null) user.cassettes.remove(cassetteId)

        if(userAccepted) user.songsAccepted++
    }

    /**
     * CassetteCaseFragment Functions
     */
    private fun retrieveCassettes(){
        cassettes.value = mutableListOf()

        UserDB.getDocumentFromId(uid)
            .addOnSuccessListener { documentSnapshot ->
                val userCassettes = documentSnapshot["cassettes"] as ArrayList<*>

                for (userCassette in userCassettes) {
                    CassetteDB.getDocumentFromId(userCassette.toString())
                        .addOnSuccessListener { cassetteDocument ->
                            val cassette = cassetteDocument.toObject(Cassette::class.java)
                            cassette?.id = cassetteDocument.id

                            if (cassette != null) cassettes.addAndUpdate(cassette)
                        }
                }
            }
    }

    private fun getFriendsForStories(){


            }



    

    /**
     * MessagesFragment functions
     */
    fun startListeningToChats(){

        ChatDB.listenToChat(uid) { querySnapshot ->

            chats.value = mutableListOf()

            val chatsList = mutableListOf<Chat>()

            querySnapshot.map { document ->
                chatsList.add(document.toObject(Chat::class.java))
            }

            chatsList.map { chat ->
                val timestamp = chat.lastMessageSent
                val lastMessageText = chat.messages.last().text
                val lastMessageRead = chat.messages.last().read
                val lastMessageSentByMe = chat.messages.last().senderId == uid
                val chatId = chat.id
                val otherUid = getTheOtherUid(chat.uids, uid) ?: return@listenToChat

                UserDB.getDocumentFromId(otherUid).addOnSuccessListener { document ->
                    val otherUser = document.toObject(User::class.java)
                    val displayedChat = DisplayedChat(
                        otherUser?.uid ?: "",
                        otherUser?.image ?: "",
                        otherUser?.name ?: "",
                        otherUser?.status ?: "",
                        lastMessageText,
                        lastMessageRead,
                        lastMessageSentByMe,
                        timestamp,
                        chatId
                    )

                    chats.addAndUpdate(displayedChat)
                }
            }
        }
    }

    fun stopListeningToChats(){
        ChatDB.detachChatListener()
    }

}
