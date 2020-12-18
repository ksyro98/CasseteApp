package com.syroniko.casseteapp.mainClasses

import android.os.Parcelable
import com.syroniko.casseteapp.spotifyClasses.SpotifyTrack
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

//import java.time.LocalDateTime

const val NO_RECEIVER_YET = "no_receiver_yet"

@Parcelize
data class Cassette(
    val senderId: String? = "",
    val track: SpotifyTrack = SpotifyTrack(),
    val comment: String = "",
    val genre: String? = "",
//    val possibleReceivers: ArrayList<String> = arrayListOf(),
    var receiver: String = "",
    val restrictedReceivers: ArrayList<String?> = arrayListOf(),
    var received: Boolean = false,
    var id: String = ""
) : Parcelable{

//    @IgnoredOnParcel
//    private var cassetteId: String = ""
//
//    fun getId() = cassetteId
//    fun setId(id: String) {
//        cassetteId = id
//    }
}