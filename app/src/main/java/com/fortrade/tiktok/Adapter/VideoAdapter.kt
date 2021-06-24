package com.fortrade.tiktok.Adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fortrade.tiktok.HomeFragmentDirections
import com.fortrade.tiktok.R
import com.fortrade.tiktok.diffUtils.VideosDiffUtils
import com.fortrade.tiktok.room.Liked
import com.fortrade.tiktok.room.VideoDao
import com.fortrade.tiktok.room.VideoDatabase
import com.fortrade.tiktok.room.VideoRepository
import com.fortrade.tiktok.utils.Constants
import com.fortradestudio.custom.AuditionContentView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_video.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread


class VideoAdapter(arrVideo: ArrayList<VideoModel>, val context: Context,val view:View) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    var arrVideoModel: ArrayList<VideoModel> = arrVideo
    val dao = VideoDatabase.getDatabase(context).getVideoDao()

    val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        )
    }

    var loadMore: (() -> Unit)? = null

    fun setLoadMoreAction(l: () -> Unit) {
        loadMore = l
    }


    override fun getItemCount(): Int {
        return arrVideoModel.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.setVideoData(arrVideoModel[position], dao, context,view)
        if (position + 1 == arrVideoModel.size) {
            // this is the last item
            loadMore?.invoke()
        }
    }

    fun updateVideoList(newList: List<VideoModel>) {
        val videoUtilsCallback = VideosDiffUtils(newList, arrVideoModel)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        arrVideoModel.clear()
        arrVideoModel.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    fun updateVideoList(newList: List<VideoModel>) {
        val videoUtilsCallback = VideosDiffUtils(newList, arrVideoModel)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        arrVideoModel.clear()
        arrVideoModel.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }
  
    fun refresh(newList: List<VideoModel>) {
        val videoUtilsCallback = VideosDiffUtils(newList, arrVideoModel)
        val diff = DiffUtil.calculateDiff(videoUtilsCallback)
        arrVideoModel.clear()
        arrVideoModel.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    data class user(
        val fullName: String = "",
        val userName: String = "",
        val phoneNumber: String = "",
        val bio: String = "",
        val website: String = "",
        val gender: String = "",
        val birthOfDate: String = "",
        val profileImageUrl: String? = null
    )

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setVideoData(videoModel: VideoModel, dao: VideoDao, context: Context,view: View) {

            val ioScope = CoroutineScope(Dispatchers.IO)

            itemView.auditionContentView.setChildText(videoModel.likes)
            itemView.videoView.setVideoPath(videoModel.videoUrl)

            itemView.profileBar.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToUserProfileFragment(videoModel.uploaderKey)
                Navigation.findNavController(view).navigate(action)
            }

            itemView.shareButton.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    Constants.linkPrefix.plus(videoModel.uniqueVideoId)
                )
                context.startActivity(Intent.createChooser(shareIntent, "Share using"))
            }

            FirebaseDatabase.getInstance()
                .getReference(Constants.userProfileData)
                .child(videoModel.uploaderKey)
                .get()
                .addOnSuccessListener {
                    val value = it.getValue(user::class.java)
                    Log.i("VideoAdapter", "setVideoData: $value")
                    if (value != null) {
                        itemView.usernameText.text = value.userName
                    }
                    Glide.with(context)
                        .asDrawable()
                        .fitCenter()
                        .placeholder(R.drawable.man_placeholder)
                        .load(value?.profileImageUrl)
                        .into(itemView.profileBar)
                }

            var spam = 0
            itemView.likeButton.setOnClickListener {
                if (itemView.auditionContentView.state == 0 && spam == 0) {
                    itemView.auditionContentView.setChildText(
                        videoModel.likes.toDouble().plus(1).toString(),
                        true,
                        R.drawable.ic_heart_fill_withbg,
                        R.drawable.ic_heart_unfill_withbg
                    )
                    videoModel.likes = videoModel.likes.toDouble().plus(1).toString()
                    spam=1

                } else if (itemView.auditionContentView.state == 1) {
                    itemView.auditionContentView.setChildText(
                        videoModel.likes.toDouble().minus(1).toString(),
                        true,
                        R.drawable.ic_heart_fill_withbg,
                        R.drawable.ic_heart_unfill_withbg
                    )
                    spam=0
                    videoModel.likes = videoModel.likes.toDouble().minus(1).toString()
                }

                ioScope.launch {
                    val repo = VideoRepository(dao)
                    val likedVideo: Liked = try {
                        repo.getLikedVideo(videoModel.uniqueVideoId).first()
                    } catch (e: Exception) {
                        Liked(0, "s")
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

            var state: Boolean = false
            itemView.videoView.setVideoPath(videoModel.videoUrl)
            Log.i("VideoAdapter", "setVideoData: ${videoModel.videoUrl}")

            var tapCounter = 0
            var mediaPlayer: MediaPlayer? = null

            val function = {
                tapCounter = if (tapCounter >= 2) {
                    // then we run the user function
                    Log.i("VideoAdapter", "Double Tap: ")
                    itemView.auditionContentView.like(R.drawable.ic_heart_gill,true)
                    itemView.auditionContentView.state = 0
                    itemView.likeButton.callOnClick()
                    itemView.auditionContentView.state=1
                    0
                } else {
                    Log.i("VideoAdapter", "setVideoData: single")
                    state = if (!state) {
                        mediaPlayer?.setVolume(0f, 0f)
                        itemView.auditionContentView.mute(true)
                        true
                    } else {
                        mediaPlayer?.setVolume(1f, 1f)
                        itemView.auditionContentView.mute(false)
                        false
                    }
                    0;
                }
            }
            val postDelayed = Handler()

            val waitHandler = {
                if(tapCounter>=2){
                    postDelayed.removeCallbacks(function)
                }else{
                    postDelayed.postDelayed(function, 220)
                }
            }

            itemView.videoView.setOnTouchListener { v, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    Log.i("VideoAdapter", "setVideoData: downnn")
                    ++tapCounter;
                    waitHandler()
                }
                if(event?.action == MotionEvent.ACTION_MOVE){
                    postDelayed.removeCallbacks(function)
                }
                return@setOnTouchListener true
            }

           itemView.videoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: MediaPlayer) {
                    mediaPlayer = mp
                    itemView.progressBar.visibility = View.GONE
                    mp.start()

                }

            })

            itemView.videoView.setOnCompletionListener {
                it.start()
//                val repo = VideoRepository(dao)
//                ioScope.launch {
//                    repo.insertToDatabase(videoModel.uniqueVideoId)
//                }
            }
        }
    }
}