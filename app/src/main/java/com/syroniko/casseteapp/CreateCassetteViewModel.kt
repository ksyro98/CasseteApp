package com.syroniko.casseteapp

import com.syroniko.casseteapp.mainClasses.Cassette
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.User

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

private const val THIRTY_MINS = 1800000

class CreateCassetteViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    var editTextLastWrittenText : String = ""


    init {

    }


}
