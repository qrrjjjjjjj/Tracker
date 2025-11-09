package com.lyciv.tracker.model

data class NotificationItem(
    val id: Long,
    val packageName: String,
    val appLabel: String,
    val title: String,
    val text: String,
    val postedAt: Long
)
