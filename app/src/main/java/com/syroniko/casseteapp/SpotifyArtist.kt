package com.syroniko.casseteapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifyArtist(val artistName: String, val artistId: String, val imageUrl: String) : Parcelable, SpotifyResult{
    override fun getClass(): String {
        return artist
    }
}