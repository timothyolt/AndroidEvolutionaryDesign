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
import com.timothyolt.evolutionarydesign.networking.asUrlReadBytes
import kotlinx.coroutines.*
import java.net.HttpURLConnection

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

    fun recycle() {
        coroutineScope.coroutineContext.job.cancelChildren()
        itemView.findViewById<ImageView>(R.id.image).setImageDrawable(null)
    }
}

suspend fun getBitmap(imageUrl: String): Bitmap = withContext(Dispatchers.IO) {
    val bytes = imageUrl.asUrlReadBytes {
        (this as HttpURLConnection).requestMethod = "GET"
    }

    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
