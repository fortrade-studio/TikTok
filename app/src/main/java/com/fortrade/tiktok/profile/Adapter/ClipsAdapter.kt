package com.fortrade.tiktok.profile.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import kotlinx.android.synthetic.main.item_clips.view.*
import kotlinx.android.synthetic.main.item_gallery.view.*

class ClipsAdapter(

    var images: List<Int>
) : RecyclerView.Adapter<ClipsAdapter.ClipsViewHolder>() {

    inner class ClipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clips, parent, false)
        return ClipsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClipsViewHolder, position: Int) {

        val curImage = images[position]
        holder.itemView.clips_thumbnail.setImageResource(curImage)

    }

    override fun getItemCount(): Int {
        return images.size
    }
}