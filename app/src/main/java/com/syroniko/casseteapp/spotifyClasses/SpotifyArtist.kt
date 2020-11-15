package com.syroniko.casseteapp.spotifyClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifyArtist(val artistName: String, val artistId: String, val imageUrl: String) : Parcelable,
    SpotifyResult {
    override fun getSpotifyClass(): String {
        return ARTIST
    }
}