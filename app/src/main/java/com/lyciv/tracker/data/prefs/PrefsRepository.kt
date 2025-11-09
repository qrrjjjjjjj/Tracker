package com.lyciv.tracker.data.prefs

import kotlinx.coroutines.flow.Flow

class PrefsRepository(private val userPrefs: UserPrefs) {
    val selectedPackages: Flow<Set<String>> = userPrefs.selectedPackages

    suspend fun togglePackage(packageName: String, isSelected: Boolean) {
        if (isSelected) {
            userPrefs.addPackage(packageName)
        } else {
            userPrefs.removePackage(packageName)
        }
    }

    suspend fun setSelectedPackages(packages: Set<String>) {
        userPrefs.setSelectedPackages(packages)
    }
}
