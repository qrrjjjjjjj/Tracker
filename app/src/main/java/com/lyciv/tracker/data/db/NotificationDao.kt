package com.lyciv.tracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY postedAt DESC LIMIT 500")
    fun getAllFlow(): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}
