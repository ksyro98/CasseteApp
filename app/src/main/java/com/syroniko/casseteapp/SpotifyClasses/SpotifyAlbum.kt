package com.syroniko.casseteapp.SpotifyClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifyAlbum(val albumName: String, val albumId: String, val imageUrl: String) :
    Parcelable, SpotifyResult {
    override fun getClass(): String {
        return ALBUM
    }
}