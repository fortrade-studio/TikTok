package com.leeladher.video
import android.content.Context
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
import com.fortrade.tiktok.room.*
import com.fortrade.tiktok.utils.Constants
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_video.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoAdapter(arrVideo:ArrayList<VideoModel>,val context:Context) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    var arrVideoModel:ArrayList<VideoModel> = arrVideo
    val dao = VideoDatabase.getDatabase(context).getVideoDao()

    val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video,parent,false))
    }

    var loadMore: (() -> Unit)? = null

    fun setLoadMoreAction(l: () -> Unit) {
        loadMore = l
    }


    override fun getItemCount(): Int {
        return arrVideoModel.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.setVideoData(arrVideoModel[position], dao)
        if (position + 1 == arrVideoModel.size) {
            // this is the last item
            loadMore?.invoke()
        }
    }

    fun updateVideoList(newList:List<VideoModel>){
        val videoUtilsCallback = VideosDiffUtils(newList,arrVideoModel)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        arrVideoModel.clear()
        arrVideoModel.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    fun refresh(newList: List<VideoModel>){
        val videoUtilsCallback = VideosDiffUtils(newList,arrVideoModel)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        arrVideoModel.clear()
        arrVideoModel.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun setVideoData(videoModel: VideoModel,dao:VideoDao){

            val ioScope = CoroutineScope(Dispatchers.IO)

            itemView.auditionContentView.setChildText(videoModel.likes)
            itemView.videoView.setVideoPath(videoModel.videoUrl)
            itemView.likeButton.setOnClickListener {
                if (itemView.auditionContentView.state==0) {
                    itemView.auditionContentView.setChildText(
                        videoModel.likes.toDouble().plus(1).toString(),true,R.drawable.ic_heart_fill_withbg,R.drawable.ic_heart_unfill_withbg
                    )
                    videoModel.likes = videoModel.likes.toDouble().plus(1).toString()

                }else if(itemView.auditionContentView.state==1){
                    itemView.auditionContentView.setChildText(
                        videoModel.likes.toDouble().minus(1).toString(),true,R.drawable.ic_heart_fill_withbg,R.drawable.ic_heart_unfill_withbg
                    )
                    videoModel.likes = videoModel.likes.toDouble().minus(1).toString()
                }

                ioScope.launch {
                    val repo = VideoRepository(dao)
                    val likedVideo : Liked = try {
                         repo.getLikedVideo(videoModel.uniqueVideoId).first()
                    }catch (e:Exception){
                        Liked(0,"s")
                    }
                    if (likedVideo.videoPublicId != videoModel.uniqueVideoId) {
                        FirebaseDatabase.getInstance()
                            .getReference(Constants.content)
                            .child(Constants.general)
                            .child(videoModel.uniqueVideoId)
                            .child("likes")
                            .get()
                            .addOnSuccessListener {
                                val toDouble = it.value?.toString()?.toDouble()
                                ioScope.launch {
                                    FirebaseDatabase.getInstance()
                                        .getReference(Constants.content)
                                        .child(Constants.general)
                                        .child(videoModel.uniqueVideoId)
                                        .child("likes")
                                        .setValue(toDouble?.plus(1).toString())
                                        .addOnSuccessListener {
                                            // like is finally added
                                            // we need to add the liked video to db to show it as liked the
                                            // next time user opens the app
                                            ioScope.launch {
                                                val repo = VideoRepository(dao)
                                                repo.insertLikedVideo(videoModel.uniqueVideoId)
                                            }
                                        }
                                }
                            }
                    }
                }

            }

            itemView.videoView.setVideoPath(videoModel.videoUrl)
            itemView.videoView.setOnPreparedListener(object :MediaPlayer.OnPreparedListener{
                override fun onPrepared(mp: MediaPlayer) {
                    itemView.progressBar.visibility = View.GONE
                    mp.start()

                }

            })

            itemView.videoView.setOnCompletionListener {
                it.start()
                val repo = VideoRepository(dao)
                ioScope.launch {
                    repo.insertToDatabase(videoModel.uniqueVideoId)
                }
            }
        }

    }

}