package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fortrade.tiktok.room.VideoDatabase
import com.fortrade.tiktok.room.VideoRepository
import com.fortrade.tiktok.utils.Constants
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragmentViewModel(
    activity: Activity,
    view:View
) : ViewModel() {

    // we need to fetch the link of the videos from the firebase realdb

    val videos = ArrayList<VideoModel>()
    val segmentVideos = ArrayList<VideoModel>()
    val n = 2
    var counter = 1;
    val dao = VideoDatabase.getDatabase(activity).getVideoDao()
    val ioScope = CoroutineScope(Dispatchers.IO)
    val repo = VideoRepository(dao)

    val firebaseDatabase = FirebaseDatabase.getInstance()
        .getReference(Constants.content)
        .child(Constants.general)


    fun getVideos(onFetched:(ArrayList<VideoModel>)->Unit,clearPrevious: Boolean = false) = CoroutineScope(Dispatchers.IO).launch {
        if(clearPrevious){
            segmentVideos.clear()
            videos.clear()
        }
        firebaseDatabase.get()
            .addOnSuccessListener { dataSnapshot ->
                ioScope.launch {
                    val list = dataSnapshot.children.map {
                        val value = it.getValue(VideoModel::class.java)
                        value!!.uniqueVideoId = it.key.toString()
                        value
                    }.filter {
                        !repo.checkIfExists(it.uniqueVideoId)
                    }
                    val temp = list.shuffled()
                    segmentVideos.addAll(temp.take(n))
                    onFetched(segmentVideos)
                    videos.addAll(temp)
                }
            }
    }

    fun inflateSegment(onInflated:(ArrayList<VideoModel>)->Unit){
        ++counter;
        segmentVideos.clear()
        segmentVideos.addAll(videos.take(counter*n))
        onInflated(segmentVideos)
    }

}