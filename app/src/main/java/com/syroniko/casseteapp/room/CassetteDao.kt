package com.syroniko.casseteapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CassetteDao {

    @Query("select * from LocalCassette")
    suspend fun getAll(): List<LocalCassette>

    @Insert
    suspend fun insertAll(vararg localCassettes: LocalCassette)

    @Delete
    suspend fun delete(localCassette: LocalCassette)

}