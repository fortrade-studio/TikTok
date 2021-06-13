package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeFragmentViewModelFactory(
    val activity: Activity,
    val view : View
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeFragmentViewModel(activity,view) as T
    }
}