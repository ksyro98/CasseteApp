package com.syroniko.casseteapp.firebase

interface AuthCallback {

    fun onSuccess(uid: String?)

    fun onFailure()
}