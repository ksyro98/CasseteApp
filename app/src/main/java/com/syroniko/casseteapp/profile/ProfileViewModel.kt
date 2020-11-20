package com.syroniko.casseteapp.profile

import android.app.Application
import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.User
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    application: Application,
    @Assisted savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    var uid = ""
        set(value) {
            field = value
            UserDB.getDocumentFromId(uid)
                .addOnSuccessListener { documentSnapshot ->
                    user.value = documentSnapshot.toObject(User::class.java) ?: return@addOnSuccessListener
                    isFriend.value = user.value?.friends?.contains(currentUid) ?: false
                }
        }
    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    var currentUid: String = ""
//    var isFriend = false
    val isFriend: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
}