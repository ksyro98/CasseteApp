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
    var status : String,
    var receivedLastCassetteAt: Long,
    var image: Bitmap? = null,
    var uid: String?,
    var country:String?,
    var genres: ArrayList<String> = arrayListOf(),
    var friends: ArrayList<String> = arrayListOf(),
    var cassettes: ArrayList<String> = arrayListOf(),
    var songsSent: Int = 0,
    var songsAccepted: Int = 0,
    val favoriteArtists: ArrayList<String> = arrayListOf(),
    var bio: String = "",
    val interests: ArrayList<String> = arrayListOf(),
    val cassettesAccepted: ArrayList<String> = arrayListOf()): Parcelable


// var cassettes: ArrayList<Cassette>?)
