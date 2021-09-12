package com.timothyolt.evolutionarydesign.upload

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.timothyolt.evolutionarydesign.concurrency.ProgressTracker
import com.timothyolt.evolutionarydesign.networking.*
import com.timothyolt.evolutionarydesign.notifications.Notifications
import com.timothyolt.evolutionarydesign.requireInjector
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.util.*

class UploadService : Service() {

    interface Dependencies {
        val notifications: Notifications
        val accessToken: String
        val uploadNotificationId: Int
    }

    private lateinit var dependencies: Dependencies

    private val lifecycleScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val progressTracker = ProgressTracker()

    companion object {
        fun createIntent(context: Context, imageUri: String) =
            Intent(context, UploadService::class.java)
                .putExtra(EXTRA_IMAGE_URI, imageUri)

        private fun Intent.getImageUri() = getStringExtra(EXTRA_IMAGE_URI)

        private const val EXTRA_IMAGE_URI = "imageUri"
    }

    override fun onCreate() {
        super.onCreate()
        dependencies = requireInjector().inject(this)
        lifecycleScope.launch {
            progressTracker.isActive.collect { isActive ->
                if (isActive) {
                    val uploadNotification = dependencies.notifications.createNotification()
                    startForeground(dependencies.uploadNotificationId, uploadNotification)
                } else {
                    stopForeground(true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleScope.launch {
            progressTracker.inProgress {
                uploadImage(intent)
            }
        }.invokeOnCompletion {
            stopSelf(startId)
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private suspend fun uploadImage(intent: Intent?) {
        val imageUriString = intent?.getImageUri()
        val imageUri = Uri.parse(imageUriString)

        @Suppress("BlockingMethodInNonBlockingContext")
        val imageStream = withContext(Dispatchers.IO) {
            contentResolver.openInputStream(imageUri)!!
        }

        uploadImage(imageStream)
    }

    private suspend fun uploadImage(image: InputStream) = withContext(Dispatchers.IO) {
        // ok to write this to a string rather than totally streaming it, because Imgur caps uploads to 10 MB
        val imageBytes = ByteArrayOutputStream()
        image.transferTo(Base64.getEncoder().wrap(imageBytes))
        val pngBase64String = String(imageBytes.toByteArray())

        "https://api.imgur.com/3/upload".asUrl().connection<HttpURLConnection, Unit> {
            addRequestProperty("Authorization", "Bearer ${dependencies.accessToken}")
            writeFormData(listOf(
                "image" to pngBase64String,
                "type" to "base64"
            ))
            val responseCode = responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bytes = readBytes()
                Log.w("ImgurUpload", String(bytes))
            } else {
                val errorBody = readErrorBytes()
                Log.e("ImgurUpload", String(errorBody))
                error("Non-OK status $responseCode")
            }
        }
    }

}
