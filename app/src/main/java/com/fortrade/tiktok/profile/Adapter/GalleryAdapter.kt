package com.fortrade.tiktok.profile.Adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortrade.tiktok.diffUtils.GalleryDiffUtils
import com.fortrade.tiktok.diffUtils.VideosDiffUtils
import com.fortrade.tiktok.profile.SharedViewModel
import com.fortradestudio.custom.ProfileImagesViewGroup
import com.fortradestudio.custom.RemoveButtonListener
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.item_gallery.view.*

class GalleryAdapter(
    var imageUrl: ArrayList<String>, private val listener: OnItemClickListener,
    val isUserProfile: Boolean = true, val context: Context, val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    companion object {
        private const val TAG = "GalleryAdapter"
    }

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: ProfileImagesViewGroup = itemView.gallery_image
        val crossButton: Button = itemView.crossButton

        init {

            itemView.gallery_image.setOnOpenImageClick {
                // this is where we will show the image in the square mode
                val view = LayoutInflater.from(context).inflate(R.layout.image_full, null)
                val fullImageView = view.findViewById<ImageView>(R.id.fullImageView)
                AlertDialog.Builder(context)
                    .setView(view)
                    .show()
                itemView.gallery_image.bitmapImage
                    .observe(lifecycleOwner) {
                        fullImageView.setImageBitmap(it)
                    }
            }
            if (isUserProfile) {
                itemView.gallery_image.setOnClickListener {
                    // this is to open the image picker for user to pick an image from
                    // local images

                    listener.onItemClick(adapterPosition, 0)
                }
            } else {
                itemView.gallery_image.setOnClickListener {
                    // this is when a user clicks on one of the other user's images
                    // we need to show them in full screen mode or somewhat bigger size

                    Toast.makeText(itemView.context, "image is present", Toast.LENGTH_SHORT).show()
                }

            }
        }

        override fun onClick(v: View?) {
            if (isUserProfile) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, 0)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

        if (imageUrl.size > position && imageUrl[position] != null) {
            val curImage = imageUrl[position]
            Log.i(TAG, "onBindViewHolder: $curImage")
            holder.itemView.gallery_image
                .setBitmapViaUrl(curImage)
            if (isUserProfile) {
                holder.crossButton.setOnClickListener(object : RemoveButtonListener() {
                    override fun onClick(v: View?) {
                        super.onClick(v)
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position, 1)
                        }

                    }
                })
            }
        } else {

        }

    }

    override fun getItemCount(): Int {
        return if (isUserProfile) 8 else imageUrl.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, status: Int)
    }


}
