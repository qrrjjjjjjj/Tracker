package com.lyciv.tracker.model

import android.graphics.drawable.Drawable

data class TrackedApp(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val isSelected: Boolean = false
)
