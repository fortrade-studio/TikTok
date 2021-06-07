package com.fortrade.tiktok

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.VideoView
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.item_video.*
import java.io.File


class CameraFragment : Fragment() {

//    private lateinit var videoUri: Uri
    private lateinit var cheptureVideo: ImageButton
    private lateinit var videoRec: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        cheptureVideo = view.findViewById(R.id.cheptureVideo)
        videoRec = view.findViewById(R.id.videoRec)
//        cheptureVideo.setOnClickListener {
//            recodeVideo()
//        }

        cheptureVideo.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10)
            startActivityForResult(intent,1)
        }
        return view
    }
//
//    private fun recodeVideo() {
//        val videoFile = createVideoFile()
//        if (videoFile != null){
//            videoUri = FileProvider.getUriForFile(
//                requireContext(),
//                "com.video.recode.fileprovider",
//                videoFile
//            )
//            val intent  = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//            intent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri)
//
//            startActivityForResult(intent,1)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if (requestCode == 1 && resultCode == Activity.RESULT_OK){
           videoRec.setVideoURI(data?.data)
           videoRec.start()
       }

    }

//    private fun createVideoFile(): File {
//        val fileName = "My Video"
//        val storageDir = Environment.getStorageDirectory()//Environment.DIRECTORY_MOVIES
//
//        return File.createTempFile(
//            fileName,
//            ".mp4",
//            storageDir
//        )
//    }


}