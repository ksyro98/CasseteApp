package com.syroniko.casseteapp.MainClasses

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList


//could change image with String (id in firestore or something like that)


@Parcelize
class User(
    var name: String?,
    var email: String,
    var isAvailableOnChat : String,
    var receivedLastCassetteAt: Long,
    var image: Bitmap? = null,
    var uid: String?,
    var country:String?,
    var genres: ArrayList<String>?= null,
    var friends: ArrayList<String>? = null,
    var cassettes: ArrayList<String>? = null,
    var songsSent: Int = 0,
    var songsAccepted: Int = 0): Parcelable


// var cassettes: ArrayList<Cassette>?)
