package com.fortrade.tiktok.profile

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.fortrade.tiktok.HomeFragmentArgs
import com.fortrade.tiktok.R
import com.fortrade.tiktok.databinding.FragmentGalleryBinding
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import com.fortrade.tiktok.viewModel.GalleryFragmentViewModel
import com.fortrade.tiktok.viewModel.GalleryFragmentViewModelFactory
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


class GalleryFragment(
    val number : String=FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")
) : Fragment(), GalleryAdapter.OnItemClickListener {

    val phone=FirebaseAuth.getInstance().currentUser.phoneNumber.removePrefix("+91")
    var images = ArrayList<String>()

    private lateinit var progressDialog: ProgressDialog

    lateinit var galleryAdapter :GalleryAdapter
    var selectedImageUri: Uri? = null
    var imagePosition: Int = -1

    lateinit var galleryViewModel: GalleryFragmentViewModel

    companion object{
        private const val TAG = "GalleryFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_gallery, container, false)
        galleryAdapter=GalleryAdapter(images, this,true)
        galleryAdapter.notifyDataSetChanged()
        return view
    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        galleryViewModel = ViewModelProvider(this,GalleryFragmentViewModelFactory(requireActivity(),requireView())).get(GalleryFragmentViewModel::class.java)

        if(number==phone){
            galleryAdapter=GalleryAdapter(images, this,true)
            // user profile
            recycler_view_item.adapter = galleryAdapter
            recycler_view_item.layoutManager = GridLayoutManager(activity, 3)
            fetchDataFromFirebase()
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Please Wait")
            progressDialog.setCanceledOnTouchOutside(false)

            galleryViewModel.getImages( {
                galleryAdapter.imageUrl = ArrayList(it)
                galleryAdapter.notifyDataSetChanged()
            },number)

        }
        else{

            galleryAdapter=GalleryAdapter(images, this,false)
            // user profile
            recycler_view_item.adapter = galleryAdapter
            recycler_view_item.layoutManager = GridLayoutManager(activity, 3)
            fetchDataFromFirebase()
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Please Wait")
            progressDialog.setCanceledOnTouchOutside(false)

            galleryViewModel.getImages( {
                galleryAdapter.imageUrl = ArrayList(it).filter {
                    it!=null
                } as ArrayList<String>
                galleryAdapter.notifyDataSetChanged()
            },number)

        }


    }

    override fun onItemClick(position: Int, status: Int) {
        if (status == 0) {
            imagePosition = position
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 82)
        } else if (status == 1) {
            galleryAdapter.imageUrl.removeAt(position)
            galleryAdapter.notifyDataSetChanged()
            Toast.makeText(context, "Photo deleted!", Toast.LENGTH_SHORT).show()
            val ref =
                FirebaseDatabase.getInstance().getReference("userProfileData")
                    .child(number)
                    .child("UserImages")
                    .child(position.toString())
            ref.removeValue()

        }

    }

    private fun fetchDataFromFirebase() {


        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading profile...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        Toast.makeText(context, "cool", Toast.LENGTH_SHORT).show()
        progressDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 82 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            progressDialog.setMessage("Uploading Your Image...")
            progressDialog.show()
            selectedImageUri = data.data

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    //Get Download Url
                    ref.downloadUrl.addOnSuccessListener {
                        //Save Data into Firebase Database
                        val ref =
                            FirebaseDatabase.getInstance().getReference("userProfileData")
                                .child(number)
                                .child("UserImages")
                                .child(imagePosition.toString())

                        val imageUrl = it.toString()
                        ref.setValue(it.toString()).addOnSuccessListener {

                            galleryAdapter.imageUrl.add(imageUrl)
                            galleryAdapter.notifyDataSetChanged()
                            progressDialog.dismiss()


                        }
                    }
                }
        }
    }


}