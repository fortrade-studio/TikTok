package com.fortrade.tiktok.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fortrade.tiktok.R
import com.fortrade.tiktok.utils.Constants
import com.fortrade.tiktok.utils.getVideoId
import com.google.firebase.database.FirebaseDatabase
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.fragment_clips_fullscreen.*
import kotlinx.android.synthetic.main.fragment_clips_fullscreen.view.*
import kotlinx.android.synthetic.main.item_video.view.*


class ClipsFullscreenFragment : Fragment() {


    lateinit var videoURL: String
    lateinit var userNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clips_fullscreen, container, false)
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model._vidURL.observe(viewLifecycleOwner, Observer {
            setVideoData(it)
        })
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun setVideoData(videoUrl: String) {
        var mediaPlayer: MediaPlayer? = null
        fullVideoView.setVideoPath(videoUrl)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                Constants.linkPrefix.plus(videoUrl)
            )
            context?.startActivity(Intent.createChooser(shareIntent, "Share using"))
        }


        fullVideoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer) {
                mediaPlayer = mp
                progressBar.visibility = View.GONE
                mp.start()

            }

        })
    }


}