package com.syroniko.casseteapp.room

import androidx.room.*

@Dao
interface UserTimeDao {

    @Query("select * from UserAndTime")
    suspend fun getAll(): List<UserAndTime>

    @Query("select * from UserAndTime where uid like :userId")
    suspend fun get(userId: String): UserAndTime

    @Insert
    suspend fun insertAll(vararg userAndTime: UserAndTime)

    @Delete
    suspend fun delete(userAndTime: UserAndTime)

    @Update
    suspend fun update(userAndTime: UserAndTime)

}