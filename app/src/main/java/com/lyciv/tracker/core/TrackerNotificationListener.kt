package com.lyciv.tracker.core

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lyciv.tracker.data.NotificationsRepository
import com.lyciv.tracker.data.db.AppDatabase
import com.lyciv.tracker.data.db.NotificationEntity
import com.lyciv.tracker.data.prefs.UserPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TrackerNotificationListener : NotificationListenerService() {
    private lateinit var repository: NotificationsRepository
    private lateinit var userPrefs: UserPrefs
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(applicationContext)
        repository = NotificationsRepository(database.notificationDao())
        userPrefs = UserPrefs(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        scope.launch {
            val packageName = sbn.packageName
            val selectedPackages = userPrefs.selectedPackages.firstOrNull() ?: emptySet()
            
            if (packageName !in selectedPackages) return@launch

            val notification = sbn.notification
            val title = notification.extras.getCharSequence("android.title")?.toString() ?: ""
            val text = notification.extras.getCharSequence("android.text")?.toString() ?: ""
            
            val appLabel = try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                packageName
            }

            val entity = NotificationEntity(
                packageName = packageName,
                appLabel = appLabel,
                title = title,
                text = text,
                postedAt = sbn.postTime
            )

            repository.insertNotification(entity)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
