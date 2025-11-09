package com.lyciv.tracker.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import com.lyciv.tracker.model.TrackedApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Advanced hybrid app query util.
 * - Auto fallback if launcher query fails
 * - Filters only user-installed apps
 * - Case-insensitive sorting
 * - Safe against OEM quirks (MIUI, Transsion, etc.)
 */
object AppsQuery {

    private const val TAG = "AppsQuery"

    suspend fun getInstalledApps(context: Context): List<TrackedApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val result = mutableListOf<TrackedApp>()

        try {
            // Primary query: all launcher activities (normal way)
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val launcherApps = pm.queryIntentActivities(launcherIntent, 0)
            if (launcherApps.isNotEmpty()) {
                launcherApps.forEach { info ->
                    val pkg = info.activityInfo.packageName
                    try {
                        val appInfo = pm.getApplicationInfo(pkg, 0)
                        if (isUserApp(appInfo)) {
                            result.add(appInfo.toTrackedApp(pm))
                        }
                    } catch (_: Exception) {}
                }
            }

            // Fallback: direct installed apps query (covers hidden launchers/OEM issues)
            if (result.isEmpty() || result.size < 5) {
                Log.w(TAG, "Launcher query returned too few results (${result.size}), using fallback query")
                pm.getInstalledApplications(PackageManager.GET_META_DATA).forEach { appInfo ->
                    try {
                        if (isUserApp(appInfo)) {
                            result.add(appInfo.toTrackedApp(pm))
                        }
                    } catch (_: Exception) {}
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to query installed apps", e)
        }

        // remove duplicates & sort
        result.distinctBy { it.packageName }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
    }

    private fun isUserApp(appInfo: ApplicationInfo): Boolean {
        val flags = appInfo.flags
        return (flags and ApplicationInfo.FLAG_SYSTEM == 0) &&
               (flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0)
    }

    private fun ApplicationInfo.toTrackedApp(pm: PackageManager): TrackedApp {
        val safeLabel = runCatching { pm.getApplicationLabel(this).toString() }.getOrDefault(packageName)
        val safeIcon: Drawable = runCatching { pm.getApplicationIcon(this) }.getOrDefault(
            pm.defaultActivityIcon
        )
        return TrackedApp(
            packageName = packageName,
            label = safeLabel,
            icon = safeIcon,
            isSelected = false
        )
    }
}
