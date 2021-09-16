package com.timothyolt.evolutionarydesign

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.AlbumService.Album

class AlbumAdapter: RecyclerView.Adapter<ImageViewHolder>() {
    private var album: Album? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun updateAlbum(album: Album) {
        this.album = album
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind(album!!.image)

    override fun getItemCount() = if (album == null) 0 else 10
}
