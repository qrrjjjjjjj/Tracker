package com.lyciv.tracker.data

import com.lyciv.tracker.data.db.NotificationDao
import com.lyciv.tracker.data.db.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationsRepository(private val dao: NotificationDao) {
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllFlow()

    suspend fun insertNotification(notification: NotificationEntity) {
        dao.insert(notification)
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }
}
