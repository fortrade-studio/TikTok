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
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import java.lang.ClassCastException

class GalleryFragmentViewModel(
    val activity: Activity,
    val view:View
) :ViewModel(){

    companion object{
        private const val TAG = "GalleryFragmentViewMode"
    }

    fun getImages(onImagesFetched:(List<String>)->Unit,ph:String){
        FirebaseDatabase.getInstance()
            .getReference(Constants.userProfileData)
            .child(ph)
            .get()
            .addOnSuccessListener {
                val value = try {

                    it.getValue(user::class.java)

                }catch (e: DatabaseException){
                    val value = it.getValue(userMap::class.java) as userMap
                    val convertMapToUser = convertMapToUser(value)

                    val hashMap = it.child("UserImages").value as HashMap<*, String>
                    convertMapToUser.UserImages = hashMap.values.toList()

                    try {
                        val reelsMap = it.child("userVideos").value as HashMap<*,String>
                        convertMapToUser.userVideos = reelsMap.values.toList()

                    }catch (e:ClassCastException){
                        val reelsMap = it.child("userVideos").value as ArrayList<String>
                        convertMapToUser.userVideos = reelsMap
                    }
                    convertMapToUser
                }

                Log.i(TAG, "getImages: $value")
                if (value != null) {
                    onImagesFetched(value.UserImages)
                }
            }
    }

    class s : GenericTypeIndicator<HashMap<Any, String>>() {

    }

    fun convertMapToUser(u:userMap):user{
        return user(
            u.fullName,u.userName,u.phoneNumber,u.bio,u.website,u.gender,u.birthOfDate,u.profileImageUrl
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
        var UserImages:List<String> = emptyList(),
        var userVideos:List<String> = emptyList()
    )


}