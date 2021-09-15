package com.timothyolt.evolutionarydesign

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ImageViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    constructor(parent: ViewGroup): this(
        LayoutInflater.from(parent.context).inflate(R.layout.view_image, parent, false)
    )

    fun bind(getImage: suspend () -> Image) = with(itemView) {
        scope.launch {
            val image = getImage()
            findViewById<ImageView>(R.id.image).setImageBitmap(image.toBitmap())
        }
    }

    private fun Image.toBitmap() = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    fun recycle() {

    }
}
