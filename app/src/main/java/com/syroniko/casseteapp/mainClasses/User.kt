package com.syroniko.casseteapp.mainClasses

import android.os.Parcelable
import com.syroniko.casseteapp.utils.FCM_NO_TOKEN
import com.syroniko.casseteapp.utils.SPOTIFY_NO_TOKEN
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class User(
    var name: String? = "",
    var email: String = "",
    var status : String = "",
    var receivedLastCassetteAt: Long = 0,
    var image: String? = "",
    var uid: String? = "",
    var country:String? = "",
    var genres: ArrayList<String> = arrayListOf(),
    var friends: ArrayList<String> = arrayListOf(),
    var cassettes: ArrayList<String> = arrayListOf(),
    var songsSent: Int = 0,
    var songsAccepted: Int = 0,
    val favoriteArtists: ArrayList<String> = arrayListOf(),
    var bio: String = "",
    val interests: ArrayList<String> = arrayListOf(),
    val cassettesAccepted: ArrayList<String> = arrayListOf(),
    var spotifyToken: String = SPOTIFY_NO_TOKEN,
    var fcmTokens: ArrayList<String> = arrayListOf(),
    var lastOnline: Long = 0
): Parcelable {

//    fun getLastOnline(): Double {
//        return 1.0
//    }

}


// var cassettes: ArrayList<Cassette>?)
