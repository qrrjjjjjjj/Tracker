package com.lyciv.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lyciv.tracker.ui.MainScreen
import com.lyciv.tracker.ui.theme.TrackerTheme
import com.lyciv.tracker.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            TrackerTheme {
                val viewModel: MainViewModel = viewModel()
                val notifications by viewModel.notifications.collectAsState()
                val apps by viewModel.apps.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                
                val isServiceActive = NotificationManagerCompat
                    .getEnabledListenerPackages(this)
                    .contains(packageName)

                MainScreen(
                    isServiceActive = isServiceActive,
                    notifications = notifications,
                    apps = apps,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onAppToggle = viewModel::toggleAppSelection
                )
            }
        }
    }
}
