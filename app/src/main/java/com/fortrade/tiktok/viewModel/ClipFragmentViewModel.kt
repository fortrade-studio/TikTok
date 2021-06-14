package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.fortrade.tiktok.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClipFragmentViewModel(
    val activity: Activity,
    val view:View
) : ViewModel() {

    lateinit var  phoneNumber:String

    companion object{
        private const val TAG = "ClipFragmentViewModel"
    }

    val ioScope = CoroutineScope(Dispatchers.IO)

    init {
         phoneNumber = FirebaseAuth.getInstance().currentUser.phoneNumber
    }

    fun getVideos(onFetched:(ArrayList<VideoModel>)->Unit){
        FirebaseDatabase.getInstance()
            .getReference(Constants.userProfileData)
            .child(phoneNumber.removePrefix("+91"))
            .get()
            .addOnSuccessListener {
                val value = it.getValue(user::class.java)
                if (value != null) {
                    getVideoByUniqueId(onFetched,value.userVideos)
                }
            }
    }

    private fun getVideoByUniqueId(onFetchedData:(ArrayList<VideoModel>)->Unit, list: List<String>){
        val videos = ArrayList<VideoModel>()
        list.forEach {
            Log.i(TAG, "getVideoByUniqueId: $it")
            ioScope.launch {
                FirebaseDatabase.getInstance()
                    .getReference(Constants.content)
                    .child(Constants.general)
                    .child(it)
                    .get()
                    .addOnSuccessListener {
                        val value = it.getValue(VideoModel::class.java)
                        Log.i(TAG, "getVideoByUniqueId: $value")
                        if (value != null) {
                            videos.add(value)
                            onFetchedData(videos)
                        }
                    }
            }
        }
    }


    data class user(
        val fullName: String = "",
        val userName: String = "",
        val phoneNumber: String = "",
        val bio: String = "",
        val website: String = "",
        val gender: String = "",
        val birthOfDate: String = "",
        val userImages:List<String> = emptyList(),
        val userVideos:List<String> = emptyList(),
        val profileImageUrl: String? = null
    )


}