package com.timothyolt.evolutionarydesign.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.R
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
    )

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun bind(image: Image) {
        itemView.findViewById<TextView>(R.id.imageDescription).text = image.title
        val imageView = itemView.findViewById<ImageView>(R.id.image)
        imageView.contentDescription = image.description
        coroutineScope.launch {
            val bitmap = getBitmap(image.link)
            imageView.setImageBitmap(bitmap)
        }
    }

    private suspend fun getBitmap(imageUrl: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(imageUrl)
            .let { it.openConnection() as HttpURLConnection }
            .apply { requestMethod = "GET" }

        val inputStream = BufferedInputStream(connection.inputStream)

        val data = inputStream.use { inputStream.readBytes() }

        connection.disconnect()

        BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    fun recycle() {
        coroutineScope.coroutineContext.job.cancelChildren()
        itemView.findViewById<ImageView>(R.id.image).setImageDrawable(null)
    }
}