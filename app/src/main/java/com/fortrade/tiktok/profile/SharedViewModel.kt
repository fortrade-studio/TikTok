package com.fortrade.tiktok.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private var phoneNumber = MutableLiveData("1234567890")
    val userPhoneNumber: LiveData<String> = phoneNumber

    private var vidURL = MutableLiveData("")
    val _vidURL: LiveData<String> = vidURL

    private var like = MutableLiveData("")
    val _like: LiveData<String> = like

    private var uniqueVidID = MutableLiveData("")
    val _uniqueVidID: LiveData<String> = uniqueVidID

    fun savePhoneNumber(newPhoneNumber: String) {
        phoneNumber.value = newPhoneNumber
    }

    fun saveVidInfo(newVidURL: String, likes: String, uniqueVideoId: String) {
        vidURL.value = newVidURL
        like.value = likes
        uniqueVidID.value = uniqueVideoId
    }
}