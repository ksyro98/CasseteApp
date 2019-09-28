package com.syroniko.casseteapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

//import java.time.LocalDateTime

@Parcelize
class Cassette(
    val senderId: String,
    val track: String,
    val comment: String,
    val genre: String?,
//    val timestamp: Long,
    val possibleReceivers: ArrayList<String>,
    val alreadyReceived: ArrayList<String>
) : Parcelable