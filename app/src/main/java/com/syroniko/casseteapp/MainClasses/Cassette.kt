package com.syroniko.casseteapp.MainClasses

import android.os.Parcelable
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

//import java.time.LocalDateTime

@Parcelize
data class Cassette(
    val senderId: String? = "",
    val track: SpotifyTrack = SpotifyTrack(),
    val comment: String = "",
    val genre: String? = "",
    val possibleReceivers: ArrayList<String> = arrayListOf(),
    val restrictedReceivers: ArrayList<String?> = arrayListOf(),
    var received: Boolean = false
) : Parcelable{

    @IgnoredOnParcel
    private var cassetteId: String = ""

    fun getId() = cassetteId
    fun setId(id: String) {
        cassetteId = id
    }

}