package com.fortrade.tiktok.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.leeladher.video.VideoModel

class VideosDiffUtils(
    val newList:List<VideoModel>,
    val oldList:List<VideoModel>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size

    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].videoUrl == newList[newItemPosition].videoUrl
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].videoUrl == newList[newItemPosition].videoUrl
    }
}