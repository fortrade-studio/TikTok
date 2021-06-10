package com.fortrade.tiktok.profile.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortradestudio.custom.ProfileImagesViewGroup
import kotlinx.android.synthetic.main.item_gallery.view.*

class GalleryAdapter(var imageUrl: List<String>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {



    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ProfileImagesViewGroup = itemView.gallery_image
        val crossButton: Button = itemView.crossButton

        init {
           itemView.setOnTouchListener()

           }
        }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
            return GalleryViewHolder(view)
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

            val curImage = imageUrl[position]
            holder.itemView.gallery_image.setBitmapViaUrl(curImage)



        }

        override fun getItemCount(): Int {
            return imageUrl.size
        }


    }




