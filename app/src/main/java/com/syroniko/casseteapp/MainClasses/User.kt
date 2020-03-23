package com.syroniko.casseteapp.MainClasses

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList


//could change image with String (id in firestore or something like that)


@Parcelize
data class User(
    var name: String?,
    var email: String,
    var receivedLastCassetteAt: Long,
//    var image: Bitmap? = null,
    var uid: String?,
    var country:String?,
    var genres: ArrayList<String>?,
    var friends: ArrayList<String>?,
    var cassettes: ArrayList<String>?): Parcelable

// var cassettes: ArrayList<Cassette>?)
