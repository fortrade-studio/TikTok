package com.fortrade.tiktok.profile.Adapter

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortrade.tiktok.diffUtils.ClipDiffUtils
import com.fortrade.tiktok.diffUtils.VideosDiffUtils
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.item_clips.view.*
import kotlinx.android.synthetic.main.item_gallery.view.*

class ClipsAdapter(
    var videoList: ArrayList<VideoModel>, private val listener: OnItemClickListener
) : RecyclerView.Adapter<ClipsAdapter.ClipsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clips, parent, false)
        return ClipsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClipsViewHolder, position: Int) {
        holder.itemView.videoView.setVideoURI(Uri.parse(videoList[position].videoUrl))
        holder.itemView.videoView.setOnPreparedListener {
            it.setVolume(0f, 0f)
            it.start()
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun dispatchUpdates(newList: List<VideoModel>) {
        val videoUtilsCallback = ClipDiffUtils(videoList, newList)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        videoList.clear()
        videoList.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }


    inner class ClipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        init {
            itemView.videoView.setOnClickListener {


                listener.onItemClick(adapterPosition,videoList[position].videoUrl, videoList[position].likes, videoList[position].uniqueVideoId)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            listener.onItemClick(position,videoList[position].videoUrl, videoList[position].likes, videoList[position].uniqueVideoId)
        }


    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, vidURL: String, likes: String, uniqueVideoId: String)
    }

}