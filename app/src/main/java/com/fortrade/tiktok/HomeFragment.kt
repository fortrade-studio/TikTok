package com.fortrade.tiktok

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fortrade.tiktok.utils.getVideoId
import com.fortrade.tiktok.viewModel.HomeFragmentViewModel
import com.fortrade.tiktok.viewModel.HomeFragmentViewModelFactory
import com.leeladher.video.VideoAdapter
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    var arrVideoModel = ArrayList<VideoModel>()
    lateinit var videoAdapter: VideoAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var Upload: Button


    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflate = inflater.inflate(R.layout.fragment_home, container, false)
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = requireActivity().intent.data

        if(data!=null){
            // user navigate through dynamic link
            val videoId = getVideoId(data.toString())
            Toast.makeText(requireContext(),videoId, Toast.LENGTH_SHORT).show()
        }


        homeFragmentViewModel = ViewModelProvider(
            this, HomeFragmentViewModelFactory(
                requireActivity(),
                requireView()
            )
        ).get(HomeFragmentViewModel::class.java)

        Upload = view.findViewById(R.id.Upload)

        viewPager2 = view.findViewById(R.id.viewPager)
        videoAdapter = VideoAdapter(arrVideoModel)
        viewPager2.adapter = videoAdapter


        homeFragmentViewModel.getVideos {
            videoAdapter.updateVideoList(it)
        }

        Upload.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }

    }

}