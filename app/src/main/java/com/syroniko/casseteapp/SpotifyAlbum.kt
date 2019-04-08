package com.syroniko.casseteapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifyAlbum(val albumName: String, val imageUrl: String) : Parcelable, SpotifyResult{
    override fun getClass(): String {
        return album
    }
}