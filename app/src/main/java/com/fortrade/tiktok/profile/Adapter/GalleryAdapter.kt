package com.fortrade.tiktok.profile.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortradestudio.custom.ProfileImagesViewGroup
import com.fortradestudio.custom.RemoveButtonListener
import kotlinx.android.synthetic.main.item_gallery.view.*

class GalleryAdapter(var imageUrl: List<String>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    companion object {
        private const val TAG = "GalleryAdapter"
    }

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: ProfileImagesViewGroup = itemView.gallery_image
        val crossButton: Button = itemView.crossButton

        init {

            itemView.gallery_image.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

        if (imageUrl.size > position) {
            val curImage = imageUrl[position]
            holder.itemView.gallery_image.setBitmapViaUrl(curImage)
            holder.crossButton.setOnClickListener(object : RemoveButtonListener() {
                override fun onClick(v: View?) {
                    super.onClick(v)
                    Toast.makeText(holder.itemView.context, "Photo deleted!", Toast.LENGTH_SHORT)
                        .show()

                }
            })
        } else {

        }

    }

    override fun getItemCount(): Int {
        return 8
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}
