package com.timothyolt.evolutionarydesign.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

class Notifications(
    private val context: Context,
    private val uploadChannelId: String
) {
    private fun NotificationManager.createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // you can create multiple channels and deliver different type of notifications through different channels
            val notificationChannel =
                NotificationChannel(uploadChannelId, "Upload Progress", NotificationManager.IMPORTANCE_DEFAULT)
            createNotificationChannel(notificationChannel)
        }
    }

    fun createNotification(): Notification {
        context.getSystemService<NotificationManager>()?.createNotificationChannel()
        return NotificationCompat.Builder(context, uploadChannelId)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("Uploading your image ...")
            .setProgress(0, 0, true)
            .build()
    }
}
