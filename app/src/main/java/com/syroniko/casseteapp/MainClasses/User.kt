package com.syroniko.casseteapp.MainClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//could change image with String (id in firestore or something like that)


@Parcelize
class User(
    var name: String?,
    var email: String,
//    var image: Bitmap? = null,
    var uid: String?,
    var country:String?,
    var genres: ArrayList<String>?,
    var friends: ArrayList<String>?,
    var cassettes: ArrayList<Cassette>?) : Parcelable
