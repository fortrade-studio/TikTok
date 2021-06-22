package com.fortrade.tiktok.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fortrade.tiktok.R
import com.fortrade.tiktok.profile.Adapter.ClipsAdapter
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import com.fortrade.tiktok.utils.Constants
import com.fortrade.tiktok.utils.getVideoId
import com.fortrade.tiktok.viewModel.ClipFragmentViewModel
import com.fortrade.tiktok.viewModel.ClipFragmentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.fragment_clips.*
import kotlinx.android.synthetic.main.fragment_clips_fullscreen.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.recycler_view_item
import kotlinx.android.synthetic.main.fragment_update_profile.*


class ClipsFragment(
    val number: String = FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")
) : Fragment(), ClipsAdapter.OnItemClickListener {

    var arrVideoModel = ArrayList<VideoModel>()
    private lateinit var builder: AlertDialog
    lateinit var clipFragmentViewModel: ClipFragmentViewModel
    lateinit var model: SharedViewModel

    val user_no = FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clips, container, false)
        val adapter = ClipsAdapter(ArrayList(), this)
        return view
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        clipFragmentViewModel = ViewModelProvider(
            this,
            ClipFragmentViewModelFactory(requireActivity(), requireView())
        ).get(ClipFragmentViewModel::class.java)

        if (number == user_no) {
            // this is user profile tab


        }
        val adapter = ClipsAdapter(ArrayList(), this)

        recycler_view_clips.adapter = adapter
        recycler_view_clips.layoutManager = GridLayoutManager(activity, 2)

        clipFragmentViewModel.getVideos({
            adapter.dispatchUpdates(it)
        }, number)

    }

    override fun onItemClick(position: Int, vidURL: String,  likes: String, uniqueVideoId: String) {
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.saveVidInfo(vidURL,likes,uniqueVideoId);
        findNavController().navigate(R.id.action_userProfileFragment_to_clipsFullscreenFragment2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            builder.dismiss()
            val builders = AlertDialog.Builder(context)
            val videoView: VideoView = VideoView(context)
            videoView.setVideoURI(data?.data)
            videoView.start()
            builders.setView(videoView).show()
            Toast.makeText(getActivity(), "successfully", Toast.LENGTH_SHORT).show();

        }

    }


}