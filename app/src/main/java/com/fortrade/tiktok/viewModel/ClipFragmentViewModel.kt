package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.fortrade.tiktok.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class ClipFragmentViewModel(
    val activity: Activity,
    val view: View
) : ViewModel() {

    companion object {
        private const val TAG = "ClipFragmentViewModel"
    }

    val ioScope = CoroutineScope(Dispatchers.IO)


    fun getVideos(onFetched: (ArrayList<VideoModel>) -> Unit, phoneNumber: String) {
        FirebaseDatabase.getInstance()
            .getReference(Constants.userProfileData)
            .child(phoneNumber.removePrefix("+91"))
            .get()
            .addOnSuccessListener {
                val value = try {
                    it.getValue(user::class.java)

                }catch (e: DatabaseException){
                    val value = it.getValue(userMap::class.java) as userMap
                    val convertMapToUser = convertMapToUser(value)

                    try {
                        val hashMap = it.child("UserImages").value as ArrayList<String>
                        convertMapToUser.UserImages = hashMap
                    }catch (e:ClassCastException){
                        val hashMap = it.child("UserImages").value as HashMap<*,String>
                        convertMapToUser.UserImages = hashMap.values.toList()
                    }
                    try {
                        val reelsMap = it.child("userVideos").value as HashMap<*,String>
                        convertMapToUser.userVideos = reelsMap.values.toList()
                    }catch (e:ClassCastException){
                        val reelsMap = it.child("userVideos").value as ArrayList<String>
                        convertMapToUser.userVideos = reelsMap
                    }
                    convertMapToUser
                }
                if (value != null) {
                    getVideoByUniqueId(onFetched, value.userVideos, phoneNumber)
                }
            }
    }

    private fun getVideoByUniqueId(
        onFetchedData: (ArrayList<VideoModel>) -> Unit,
        list: List<String>,
        phoneNumber: String
    ) {
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

    fun convertMapToUser(u:userMap): user {
        return user(
            u.fullName,
            u.userName,
            u.phoneNumber,
            u.bio,
            u.website,
            u.gender,
            u.birthOfDate,
            u.profileImageUrl
        )
    }

    data class userMap(
        val fullName: String = "",
        val userName: String = "",
        val phoneNumber: String = "",
        val bio: String = "",
        val website: String = "",
        val gender: String = "",
        val birthOfDate: String = "",
        val profileImageUrl: String? = null
    )


    data class user(
        val fullName: String = "",
        val userName: String = "",
        val phoneNumber: String = "",
        val bio: String = "",
        val website: String = "",
        val gender: String = "",
        val birthOfDate: String = "",
        val profileImageUrl: String? = null,
        var UserImages: List<String> = emptyList(),
        var userVideos: List<String> = emptyList(),
    )


}