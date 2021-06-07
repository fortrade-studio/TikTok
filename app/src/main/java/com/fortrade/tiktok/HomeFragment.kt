package com.fortrade.tiktok

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.leeladher.video.VideoAdapter
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    var arrVideoModel = ArrayList<VideoModel>()
    var videoAdapter: VideoAdapter? = null
    private lateinit var viewPager2: ViewPager2
    private lateinit var Upload:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        Upload = view.findViewById(R.id.Upload)
        // add video in viewpager

        arrVideoModel.add(VideoModel("title1","Comedy video","https://firebasestorage.googleapis.com/v0/b/social-media-app-a077f.appspot.com/o/______sanjuu______20210329_CMt1.mp4?alt=media&token=a5048adf-cf75-43f2-ad97-66c7824c00bc"))
        arrVideoModel.add(VideoModel("title2","Comedy video","https://firebasestorage.googleapis.com/v0/b/social-media-app-a077f.appspot.com/o/______sanjuu______20210329_CMt1.mp4?alt=media&token=a5048adf-cf75-43f2-ad97-66c7824c00bc"))
        viewPager2 = view.findViewById(R.id.viewPager)
        videoAdapter = VideoAdapter(arrVideoModel)

        viewPager2.adapter = videoAdapter

        Upload.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }
        return view
    }

}