package com.lyciv.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lyciv.tracker.data.NotificationsRepository
import com.lyciv.tracker.data.db.AppDatabase
import com.lyciv.tracker.data.prefs.PrefsRepository
import com.lyciv.tracker.data.prefs.UserPrefs
import com.lyciv.tracker.model.NotificationItem
import com.lyciv.tracker.model.TrackedApp
import com.lyciv.tracker.util.AppsQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val notificationsRepository = NotificationsRepository(database.notificationDao())
    private val prefsRepository = PrefsRepository(UserPrefs(application))

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allApps = MutableStateFlow<List<TrackedApp>>(emptyList())
    
    val notifications: StateFlow<List<NotificationItem>> = notificationsRepository.allNotifications
        .map { list -> list.map { 
            NotificationItem(
                id = it.id,
                packageName = it.packageName,
                appLabel = it.appLabel,
                title = it.title,
                text = it.text,
                postedAt = it.postedAt
            )
        }}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val apps: StateFlow<List<TrackedApp>> = combine(
        _allApps,
        prefsRepository.selectedPackages,
        _searchQuery
    ) { allApps, selectedPackages, query ->
        allApps
            .map { app -> app.copy(isSelected = app.packageName in selectedPackages) }
            .filter { app -> 
                query.isEmpty() || app.label.contains(query, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val installedApps = AppsQuery.getInstalledApps(getApplication())
            _allApps.value = installedApps
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleAppSelection(packageName: String, isSelected: Boolean) {
        viewModelScope.launch {
            prefsRepository.togglePackage(packageName, isSelected)
        }
    }
}
