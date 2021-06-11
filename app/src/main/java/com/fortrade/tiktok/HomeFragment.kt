package com.fortrade.tiktok

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fortrade.tiktok.utils.getVideoId
import com.fortrade.tiktok.viewModel.HomeFragmentViewModel
import com.fortrade.tiktok.viewModel.HomeFragmentViewModelFactory
import com.fortradestudio.custom.RemoveButtonListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.leeladher.video.VideoAdapter
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.fragment_camera.*

class HomeFragment : Fragment() {

    var arrVideoModel = ArrayList<VideoModel>()
    lateinit var videoAdapter: VideoAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var Upload: Button
    private lateinit var  builder: AlertDialog

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
            val videoView = LayoutInflater.from(context).inflate(R.layout.video_upload_dialog,null)

            val camera:Button = videoView.findViewById(R.id.camera_Btn)
            val gallery:Button = videoView.findViewById(R.id.gallery_Btn)
            val cancel_Btn:Button = videoView.findViewById(R.id.cancel_Btn)

            builder = AlertDialog.Builder(context)
                .setView(videoView).create()

            camera.setOnClickListener {
                  Toast.makeText(context,"clicked camera",Toast.LENGTH_LONG).show()
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,60)
                startActivityForResult(intent,1)
                builder.dismiss()
            }

            gallery.setOnClickListener {

           Toast.makeText(context,"clicked gallery",Toast.LENGTH_LONG).show()

                Dexter.withContext(context)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                            val intent = Intent()
                            intent.type = "video/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            startActivityForResult(intent, 8)
                        }

                        override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                        override fun onPermissionRationaleShouldBeShown(
                            permissionRequest: PermissionRequest,
                            permissionToken: PermissionToken
                        ) {
                            permissionToken.continuePermissionRequest()
                        }
                    }).check()

                builder.dismiss()
            }

            cancel_Btn.setOnClickListener {
                builder.dismiss()
                Toast.makeText(context,"clicked cancel",Toast.LENGTH_LONG).show()
            }


            builder.setCancelable(false)
            builder.show()



        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            builder.dismiss()
           val  builders = AlertDialog.Builder(context)
           val videoView:VideoView = VideoView(context)
            videoView.setVideoURI(data?.data)
            videoView.start()
            builders.setView(videoView).show()
            Toast.makeText(getActivity(),"successfully",Toast.LENGTH_SHORT).show();

        }

        if (resultCode == Activity.RESULT_OK && requestCode == 8) {
            if (data?.data != null) {
                val  builders = AlertDialog.Builder(context)
                val videoView:VideoView = VideoView(context)
                var uri: Uri = data.data!!
                videoView.setVideoURI(uri)
                var mediaController: MediaController = MediaController(context)
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, Uri.parse(uri.toString()))
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                var timeInMillisec = time?.toLong();
                retriever.release()
                if (timeInMillisec != null) {
                    if (timeInMillisec<=60000)
                    {
                        builder.dismiss()
                        videoView.setMediaController(mediaController)
                        videoView.start()
                        builders.setView(videoView).show()
                        Toast.makeText(getActivity(),"Choose File successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        builder.dismiss()
                        Toast.makeText(getActivity(),"Duration of video more than 60 seconds",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}