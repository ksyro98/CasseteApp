package com.syroniko.casseteapp.MainClasses

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack
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
) : Parcelable