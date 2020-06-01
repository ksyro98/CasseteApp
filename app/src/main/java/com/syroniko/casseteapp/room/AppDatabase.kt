package com.syroniko.casseteapp.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [LocalCassette::class, UserAndTime::class], version = 4)
abstract class AppDatabase : RoomDatabase(){

    abstract fun cassetteDao() : CassetteDao

    abstract fun userTimeDao() : UserTimeDao

}