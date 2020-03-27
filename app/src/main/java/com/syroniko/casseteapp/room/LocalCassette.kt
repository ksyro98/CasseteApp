package com.syroniko.casseteapp.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


//todo check if it works (with 1 new column) and if not fix it
@Entity
@Parcelize
data class LocalCassette(
    @PrimaryKey val cassetteId: String = "",
    @ColumnInfo val trackName: String = "",
    @ColumnInfo val senderId: String = ""
): Parcelable