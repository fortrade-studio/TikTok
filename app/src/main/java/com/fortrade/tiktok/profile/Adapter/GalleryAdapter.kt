package com.fortrade.tiktok.profile.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import kotlinx.android.synthetic.main.item_gallery.view.*

class GalleryAdapter (var images: List<Int>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

            val curImage = images[position]
            holder.itemView.gallery_image.setImageResource(curImage)

    }

    override fun getItemCount(): Int {
        return images.size
    }
}