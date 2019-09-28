package com.syroniko.casseteapp

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//could change image with String (id in firestore or something like that)


@Parcelize
class User(
    var name: String?,
    var email: String,
//    var image: Bitmap? = null,
//    val uid: String,
    val country:String?,
    val genres: ArrayList<String>?,
    val friends: ArrayList<String>?) : Parcelable
