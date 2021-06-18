package com.fortrade.tiktok.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private var phoneNumber = MutableLiveData("1234567890")
    val userPhoneNumber: LiveData<String> = phoneNumber

    private var vidURL = MutableLiveData("")
    val _vidURL: LiveData<String> = vidURL

    private var _phoneNumberClipsFragment = MutableLiveData("")
    val phoneNumberClipsFragment: LiveData<String> = _phoneNumberClipsFragment

    fun savePhoneNumber(newPhoneNumber: String) {
        phoneNumber.value = newPhoneNumber
    }

    fun saveVidURL(newVidURL: String, newPhoneNumberClipsFragment: String) {
        vidURL.value = newVidURL
        _phoneNumberClipsFragment.value = newPhoneNumberClipsFragment
    }
}