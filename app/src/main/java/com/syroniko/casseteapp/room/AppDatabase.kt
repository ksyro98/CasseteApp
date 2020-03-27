package com.syroniko.casseteapp.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [LocalCassette::class], version = 2)
abstract class AppDatabase : RoomDatabase(){

    abstract fun cassetteDao() : CassetteDao

}