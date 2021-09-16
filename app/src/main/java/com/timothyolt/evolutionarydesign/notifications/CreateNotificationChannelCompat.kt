package com.timothyolt.evolutionarydesign.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

fun NotificationManager.createNotificationChannelCompat(create: () -> NotificationChannel) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(create())
    }
}
