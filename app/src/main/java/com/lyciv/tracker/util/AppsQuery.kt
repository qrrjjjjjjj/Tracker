package com.lyciv.tracker.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.lyciv.tracker.model.TrackedApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppsQuery {
    suspend fun getInstalledApps(context: Context): List<TrackedApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val apps = pm.queryIntentActivities(mainIntent, 0)
            .mapNotNull { resolveInfo ->
                try {
                    val packageName = resolveInfo.activityInfo.packageName
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    
                    if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        TrackedApp(
                            packageName = packageName,
                            label = pm.getApplicationLabel(appInfo).toString(),
                            icon = pm.getApplicationIcon(appInfo),
                            isSelected = false
                        )
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
        
        apps
    }
}
