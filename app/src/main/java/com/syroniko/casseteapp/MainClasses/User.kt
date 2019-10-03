package com.syroniko.casseteapp.MainClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList


//could change image with String (id in firestore or something like that)


@Parcelize
class User(
    var name: String?,
    var email: String,
    var receivedLastCassetteAt: Long,
//    var image: Bitmap? = null,
    var uid: String?,
    var country:String?,
    var genres: ArrayList<String>?,
    var friends: ArrayList<String>? ): Parcelable

// var cassettes: ArrayList<Cassette>?)
