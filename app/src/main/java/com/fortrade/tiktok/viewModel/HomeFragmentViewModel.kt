package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fortrade.tiktok.utils.Constants
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragmentViewModel(
    activity: Activity,
    view:View
) : ViewModel() {

    // we need to fetch the link of the videos from the firebase realdb

    val videos = ArrayList<VideoModel>()
    val n = 50

    val firebaseDatabase = FirebaseDatabase.getInstance()
        .getReference(Constants.content)
        .child(Constants.general)


    fun getVideos(onFetched:(ArrayList<VideoModel>)->Unit) = CoroutineScope(Dispatchers.IO).launch {
        firebaseDatabase.get()
            .addOnSuccessListener { dataSnapshot ->
                val list = dataSnapshot.children.map {
                    val value = it.getValue(VideoModel::class.java)
                    value!!
                }.take(n)
                videos.addAll(list)
                onFetched(videos)
            }
    }

}