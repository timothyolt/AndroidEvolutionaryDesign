package com.timothyolt.evolutionarydesign

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class ImageViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    constructor(parent: ViewGroup): this(
        LayoutInflater.from(parent.context).inflate(R.layout.view_image, parent, false)
    )

    fun bind(image: Image) = with(itemView) {
        findViewById<ImageView>(R.id.image).setImageBitmap(image.toBitmap())
    }

    private fun Image.toBitmap() = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    fun recycle() {
        scope.coroutineContext.job.cancelChildren()
        itemView.findViewById<ImageView>(R.id.image).setImageDrawable(null)
    }
}
