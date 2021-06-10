package com.fortrade.tiktok.profile.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortradestudio.custom.ProfileImagesViewGroup
import com.fortradestudio.custom.RemoveButtonListener
import kotlinx.android.synthetic.main.item_gallery.view.*

class GalleryAdapter(var imageUrl: List<String>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    companion object{
        private const val TAG = "GalleryAdapter"
    }

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ProfileImagesViewGroup = itemView.gallery_image
        val crossButton: Button = itemView.crossButton
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
                }
            })
        }else{
            holder.itemView.gallery_image.setOnClickListener {
                Log.i(TAG, "onBindViewHolder: ")
            }
        }

    }

    override fun getItemCount(): Int {
        return 8
    }


}




