package com.timothyolt.evolutionarydesign

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.AlbumService.Album

class AlbumAdapter: RecyclerView.Adapter<ImageViewHolder>() {
    private var album: Album? = null
    set(value) {
        images = value.images
    }
    private val images = mutableListOf<Image>()
    
    fun loadAlbum(album: Album) {
        images.clear()
        images.addAll(album.images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind(album!!.getImage)

    override fun getItemCount() = 10
}