package com.syroniko.casseteapp.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syroniko.casseteapp.mainClasses.User
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class UserAndTime(
    @PrimaryKey val uid: String = "",
    @ColumnInfo val userName: String = "",
    @ColumnInfo val userImage: String = "",
    @ColumnInfo val userStatus: String = "",
    @ColumnInfo var time: Long = 0
) : Parcelable{


    constructor(user: User, time: Long) : this(user.uid ?: "", user.name ?: "", user.image ?: "", user.status, time)

    fun getUser() = User(name = userName, image = userImage, uid = uid, status = userStatus)

}