package com.fortrade.tiktok.viewModel

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fortrade.tiktok.MainActivity

class GalleryFragmentViewModelFactory(
    val activity: Activity,
    val view:View
) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GalleryFragmentViewModel(activity, view) as T
    }
}