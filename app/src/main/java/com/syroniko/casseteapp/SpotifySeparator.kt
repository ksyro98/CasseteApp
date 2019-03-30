package com.syroniko.casseteapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifySeparator(val message: String) : SpotifyResult, Parcelable{
    override fun getClass(): String {
        return separator
    }
}