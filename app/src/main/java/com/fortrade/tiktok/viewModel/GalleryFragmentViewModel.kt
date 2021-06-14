package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fortrade.tiktok.model.UserProfileData
import com.fortrade.tiktok.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GalleryFragmentViewModel(
    val activity: Activity,
    val view:View
) :ViewModel(){

    companion object{
        private const val TAG = "GalleryFragmentViewMode"
    }

    private val ph = FirebaseAuth.getInstance().currentUser.phoneNumber
    fun getImages(onImagesFetched:(List<String>)->Unit){
        FirebaseDatabase.getInstance()
            .getReference(Constants.userProfileData)
            .child(ph.removePrefix("+91"))
            .get()
            .addOnSuccessListener {
                val value = it.getValue(user::class.java)
                Log.i(TAG, "getImages: $value")
                if (value != null) {
                    onImagesFetched(value.UserImages)
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
        val profileImageUrl: String? = null,
        val UserImages:List<String> = emptyList(),
        val userVideos:List<String> = emptyList()
    )


}