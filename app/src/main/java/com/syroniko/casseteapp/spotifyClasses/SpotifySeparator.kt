package com.syroniko.casseteapp.spotifyClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifySeparator(val message: String) : SpotifyResult, Parcelable{
    override fun getSpotifyClass(): String {
        return SEPARATOR
    }
}