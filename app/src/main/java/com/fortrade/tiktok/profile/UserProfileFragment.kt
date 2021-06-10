package com.fortrade.tiktok.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.fortrade.tiktok.R
import com.fortrade.tiktok.databinding.FragmentUserProfileBinding
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import com.fortrade.tiktok.profile.Adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class UserProfileFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var database: DatabaseReference
    lateinit var binding: FragmentUserProfileBinding
    private val args by navArgs<UserProfileFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.getSupportActionBar()?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)


        database = FirebaseDatabase.getInstance().getReference("userProfileData")
        database.child(args.userNumber).get().addOnSuccessListener {


            if (it.exists()) {
                val username = it.child("userName").value
                val bio = it.child("bio").value
                val profileImage = it.child("profileImageUrl").value

                view.username_textview.text = username.toString()
                view.bio_textview.text = bio.toString()
                Picasso.with(activity).load(profileImage.toString()).into(view.user_photo)

            } else {

                Toast.makeText(context, "User does not exist!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()
        }



        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.profile_tab_layout)
        viewPager = view.findViewById(R.id.viewpager)
        setupViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)

    }


    private fun setupViewPager(viewpager: ViewPager) {
        var adapter = ViewPagerAdapter((activity as AppCompatActivity).supportFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(GalleryFragment(), "Gallery")
        adapter.addFragment(ClipsFragment(), "CLips")

        // setting adapter to view pager.
        viewpager.setAdapter(adapter)
    }






}