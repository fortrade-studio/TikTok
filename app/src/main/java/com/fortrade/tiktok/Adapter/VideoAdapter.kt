package com.leeladher.video
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortrade.tiktok.diffUtils.VideosDiffUtils
import kotlinx.android.synthetic.main.item_video.view.*

class VideoAdapter(arrVideo:ArrayList<VideoModel>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    var arrVideoModel:ArrayList<VideoModel> = arrVideo

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video,parent,false))
    }

    override fun getItemCount(): Int {
        return arrVideoModel.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.setVideoData(arrVideoModel[position])
    }

    fun updateVideoList(newList:List<VideoModel>){
        val videoUtilsCallback = VideosDiffUtils(newList,arrVideoModel)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        arrVideoModel.clear()
        arrVideoModel.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun setVideoData(videoModel: VideoModel){

            itemView.tvTitle.text = videoModel.videoTitle
            itemView.tvDesc.text = videoModel.videoDesc
            itemView.videoView.setVideoPath(videoModel.videoUrl)
            itemView.videoView.setOnPreparedListener(object :MediaPlayer.OnPreparedListener{
                override fun onPrepared(mp: MediaPlayer) {
                    itemView.progressBar.visibility = View.GONE
                    mp.start()

                }

            })

            itemView.videoView.setOnCompletionListener { object : MediaPlayer.OnCompletionListener{
                override fun onCompletion(mp: MediaPlayer) {
                    mp.start()
                }
            } }

        }

    }

}