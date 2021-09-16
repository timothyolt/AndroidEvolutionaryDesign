package com.timothyolt.evolutionarydesign.upload

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.timothyolt.evolutionarydesign.notifications.Notifications
import com.timothyolt.evolutionarydesign.notifications.createNotificationChannelCompat
import kotlinx.coroutines.*

class UploadService : Service() {

    interface Dependencies {
        val notifications: Notifications
    }

    private lateinit var dependencies: Dependencies

    private val lifecycleScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        fun createIntent(context: Context, imageUri: String) =
            Intent(context, UploadService::class.java)
                .putExtra(EXTRA_IMAGE_URI, imageUri)

        private fun Intent.getImageUri() = getStringExtra(EXTRA_IMAGE_URI)

        private const val EXTRA_IMAGE_URI = "imageUri"
    }

    override fun onCreate() {
        super.onCreate()
//        dependencies = injector().inject(this)
        startForeground(dependencies.notifications.ids.upload, createNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    private fun createNotification(): Notification {
        val uploadChannelId = dependencies.notifications.channels.uploadProgress
        getSystemService<NotificationManager>()?.createNotificationChannelCompat {
            NotificationChannel(uploadChannelId, "Upload Progress", NotificationManager.IMPORTANCE_DEFAULT)
        }
        return NotificationCompat.Builder(this, uploadChannelId)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("Uploading your image ...")
            .setProgress(0, 0, true)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleScope.launch {
            // TODO
        }.invokeOnCompletion {
            stopSelf(startId)
            stopForeground(true)
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? = null

}
