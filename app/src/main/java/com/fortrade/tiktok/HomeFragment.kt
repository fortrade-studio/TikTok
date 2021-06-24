package com.fortrade.tiktok

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.fortrade.tiktok.Adapter.VideoAdapter
import com.fortrade.tiktok.authentication.AuthFragmentDirections
import com.fortrade.tiktok.utils.Constants
import com.fortrade.tiktok.utils.getVideoId
import com.fortrade.tiktok.viewModel.HomeFragmentViewModel
import com.fortrade.tiktok.viewModel.HomeFragmentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.leeladher.video.VideoModel
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    var arrVideoModel = ArrayList<VideoModel>()
    lateinit var videoAdapter: VideoAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var Upload: Button
    private lateinit var builder: AlertDialog
    private lateinit var progressDialog: ProgressDialog

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var homeFragmentViewModel: HomeFragmentViewModel
    private lateinit var swipeRefreshListener: SwipeRefreshLayout

    val mainScope = CoroutineScope(Dispatchers.Main)

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

        swipeRefreshListener = view.findViewById(R.id.frameLayout)
        swipeRefreshListener.setOnRefreshListener(this)

        // init progressBar
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Uploading")
        progressDialog.setCanceledOnTouchOutside(false)

        if (data != null) {
            // user navigate through dynamic link
            val videoId = getVideoId(data.toString())
            Toast.makeText(requireContext(), videoId, Toast.LENGTH_SHORT).show()
        }

        homeFragmentViewModel = ViewModelProvider(
            this, HomeFragmentViewModelFactory(
                requireActivity(),
                requireView()
            )
        ).get(HomeFragmentViewModel::class.java)


        if (data != null) {
            // user navigate through dynamic link
            val videoId = getVideoId(data.toString())
            FirebaseDatabase.getInstance()
                .getReference(Constants.content)
                .child(Constants.general)
                .child(videoId)
                .get().addOnSuccessListener {
                    val value = it.getValue(VideoModel::class.java)
                    value?.let { it1 -> arrVideoModel.add(it1) }
                }
            Toast.makeText(requireContext(), videoId, Toast.LENGTH_SHORT).show()
        }


        Upload = view.findViewById(R.id.Upload)
        viewPager2 = view.findViewById(R.id.viewPager)

        videoAdapter = VideoAdapter(arrVideoModel, requireContext(), requireView())
        viewPager2.adapter = videoAdapter

        videoAdapter.setLoadMoreAction {
            homeFragmentViewModel.inflateSegment {
                mainScope.launch {
                    videoAdapter.updateVideoList(it)
                }
            }
        }

        homeFragmentViewModel.getVideos({
            mainScope.launch {
                videoAdapter.updateVideoList(it)
            }
        })


        val navigate = view.findViewById<Button>(R.id.navigate)
        navigate.setOnClickListener {
            val phoneNumber = FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")
            val action = HomeFragmentDirections.actionHomeFragmentToUserProfileFragment(phoneNumber)
            findNavController().navigate(action)
        }

        Upload.setOnClickListener {
            val videoView = LayoutInflater.from(context).inflate(R.layout.video_upload_dialog, null)

            val camera: Button = videoView.findViewById(R.id.camera_Btn)
            val gallery: Button = videoView.findViewById(R.id.gallery_Btn)
            val cancel_Btn: Button = videoView.findViewById(R.id.cancel_Btn)

            builder = AlertDialog.Builder(context)
                .setView(videoView).create()

            camera.setOnClickListener {
                Toast.makeText(context, "clicked camera", Toast.LENGTH_LONG).show()

                Dexter.withContext(context)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {

                            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
                            startActivityForResult(intent, 1)
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



            gallery.setOnClickListener {

                Toast.makeText(context, "clicked gallery", Toast.LENGTH_LONG).show()

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
                Toast.makeText(context, "clicked cancel", Toast.LENGTH_LONG).show()
            }


            builder.setCancelable(false)
            builder.show()


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            builder.dismiss()
            val builders = AlertDialog.Builder(context)
            builders.setCancelable(false)
            val videoView: VideoView = VideoView(context)
            val videoUri = data?.data

            videoView.setVideoURI(videoUri)
            videoView.start()

            builders.setPositiveButton(
                getString(R.string.Next),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show()
                        uploadVideoFirebase(videoUri)
                    }
                })
            builders.setView(videoView).show()

        }

        if (resultCode == Activity.RESULT_OK && requestCode == 8) {
            if (data?.data != null) {
                val builders = AlertDialog.Builder(context)
                builders.setCancelable(false)
                val videoView: VideoView = VideoView(context)
                var uri: Uri = data.data!!
                videoView.setVideoURI(uri)
                var mediaController: MediaController = MediaController(context)
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, Uri.parse(uri.toString()))
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                var timeInMillisec = time?.toLong();
                retriever.release()
                if (timeInMillisec != null) {
                    if (timeInMillisec <= 60000) {
                        builder.dismiss()
                        videoView.setMediaController(mediaController)
                        videoView.start()
                        builders.setPositiveButton(
                            getString(R.string.Next),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    uploadGalleryVideoFirebase(uri)
                                }
                            })

                        builders.setView(videoView).show()
                        Toast.makeText(
                            getActivity(),
                            "Choose File successfully",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        builder.dismiss()
                        Toast.makeText(
                            getActivity(),
                            "Duration of video more than 60 seconds",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun uploadVideoFirebase(videoUri: Uri?) {
        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()

        val filePathName = "content/general/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)

        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);

                val downloadUri = uriTask.result
                if (uriTask.isSuccessful) {
                    // upload video details
//                    val uid = FirebaseAuth.getInstance().currentUser.uid
                    // val hashMap = HashMap<String, Any>()
//                    hashMap["videoUrl"] = "$downloadUri"
//                    hashMap["uploaderKey"] = FirebaseAuth.getInstance().currentUser.phoneNumber
//                    hashMap["likes"] = "0"

                    var number: String =
                        FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")

                    val videoData = VideoModel("$downloadUri", "", number)


                    val dbReference =
                        FirebaseDatabase.getInstance().getReference("Content").child("general")
                    dbReference.child(timestamp)
                        .setValue(videoData)
                        .addOnSuccessListener { taskSnapshot ->
                            progressDialog.dismiss()

                            Toast.makeText(
                                getActivity(),
                                "Upload",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                getActivity(),
                                "${e.message} ",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                }

            }.addOnFailureListener {
                progressDialog.dismiss()
            }
    }

    private fun uploadGalleryVideoFirebase(videoUri: Uri?) {
        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()

        val filePathName = "content/general/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)

        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);

                val downloadUri = uriTask.result
                if (uriTask.isSuccessful) {
                    // upload video details

//                    val user = FirebaseAuth.getInstance().currentUser
//                    val uid = user.uid
//                    val profile = user.photoUrl
                    //    val videoData = VideoModel("$downloadUri")

//                    val hashMap = HashMap<String, Any>()
//
//                    hashMap["videoUri"] = "$downloadUri"

                    val number =
                        FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")
                    val videoData = VideoModel("$downloadUri", "", number)

                    val dbReference =
                        FirebaseDatabase.getInstance().getReference("Content").child("general")
                    dbReference.child(timestamp)
                        .setValue(videoData)
                        .addOnSuccessListener { taskSnapshot ->
                            progressDialog.dismiss()

                            Toast.makeText(
                                getActivity(),
                                "Upload",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                getActivity(),
                                "${e.message} ",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                }

            }.addOnFailureListener {
                progressDialog.dismiss()
            }
    }

    override fun onRefresh() {
        homeFragmentViewModel.getVideos({
            mainScope.launch {
                videoAdapter.updateVideoList(it)
                Handler().postDelayed({
                    viewPager2.currentItem = 0
                }, 100)
            }
        }, true)

        swipeRefreshListener.isRefreshing = false

    }

}