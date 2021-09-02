package com.timothyolt.evolutionarydesign.image

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter : RecyclerView.Adapter<ImageViewHolder>() {

    private var images: List<Image> = emptyList()

    fun updateImages(images: List<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun getItemCount() = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageViewHolder(parent)

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind(images[position])

    override fun onViewRecycled(holder: ImageViewHolder) = holder.recycle()

}
