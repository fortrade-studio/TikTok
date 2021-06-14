package com.fortrade.tiktok.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fortrade.tiktok.R
import com.fortrade.tiktok.profile.Adapter.ClipsAdapter
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import com.fortrade.tiktok.viewModel.ClipFragmentViewModel
import com.fortrade.tiktok.viewModel.ClipFragmentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_clips.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.recycler_view_item
import kotlinx.android.synthetic.main.fragment_update_profile.*


class ClipsFragment(
    val number:String = FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")
) : Fragment() {

    lateinit var clipFragmentViewModel: ClipFragmentViewModel

    val user_no = FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clips, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        clipFragmentViewModel = ViewModelProvider(this,ClipFragmentViewModelFactory(requireActivity(),requireView())).get(ClipFragmentViewModel::class.java)

        if(number == user_no){
            // this is user profile tab


        }

        val adapter = ClipsAdapter(ArrayList())
        recycler_view_clips.adapter = adapter
        recycler_view_clips.layoutManager = GridLayoutManager(activity,2)

        clipFragmentViewModel.getVideos ({
            adapter.dispatchUpdates(it)
        },number)

    }


}