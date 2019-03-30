package com.syroniko.casseteapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpotifyTrack(val trackName: String, val trackId: String, val artistIds: ArrayList<String>, val artistNames: ArrayList<String>, val imageUrl: String) : Parcelable, SpotifyResult{
    override fun getClass(): String {
        return track
    }
}