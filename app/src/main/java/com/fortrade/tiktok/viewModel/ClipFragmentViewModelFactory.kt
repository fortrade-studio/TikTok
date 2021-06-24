package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClipFragmentViewModelFactory(
    val activity: Activity,
    val view:View
) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClipFragmentViewModel(activity,view) as T
    }

}