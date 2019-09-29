package com.syroniko.casseteapp.MainClasses

import android.os.Parcelable
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
import kotlinx.android.parcel.Parcelize
import java.util.*

//import java.time.LocalDateTime

@Parcelize
class Cassette(
    val senderId: String?,
    val track: SpotifyTrack,
    val comment: String,
    val genre: String?,
    val possibleReceivers: ArrayList<String>,
    val restrictedReceivers: ArrayList<String?>,
    var received: Boolean = false
) : Parcelable