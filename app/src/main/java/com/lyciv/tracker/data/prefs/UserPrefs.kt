package com.lyciv.tracker.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPrefs(private val context: Context) {
    private val selectedPackagesKey = stringSetPreferencesKey("selected_packages")

    val selectedPackages: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[selectedPackagesKey] ?: emptySet()
    }

    suspend fun setSelectedPackages(packages: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[selectedPackagesKey] = packages
        }
    }

    suspend fun addPackage(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[selectedPackagesKey] ?: emptySet()
            prefs[selectedPackagesKey] = current + packageName
        }
    }

    suspend fun removePackage(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[selectedPackagesKey] ?: emptySet()
            prefs[selectedPackagesKey] = current - packageName
        }
    }
}
