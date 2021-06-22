package com.fortrade.tiktok.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.fortrade.tiktok.R
import com.fortrade.tiktok.databinding.FragmentUserProfileBinding
import com.fortrade.tiktok.profile.Adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import java.lang.IllegalStateException


class UserProfileFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var editProfile: ImageView
    lateinit var insta: ImageView
    lateinit var fb: ImageView
    private lateinit var database: DatabaseReference
    private val phoneNumber =
        FirebaseAuth.getInstance().currentUser?.phoneNumber?.removePrefix("+91")
    lateinit var binding: FragmentUserProfileBinding
    private val args by navArgs<UserProfileFragmentArgs>()
    private val sharedViewModel: SharedViewModel by activityViewModels()


    private lateinit var userProfileView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.getSupportActionBar()?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userProfileView = inflater.inflate(R.layout.fragment_user_profile, container, false)

        sharedViewModel.savePhoneNumber(args.userNumber)
        database = FirebaseDatabase.getInstance().getReference("userProfileData")
        database.child(args.userNumber).get().addOnSuccessListener {


            if (it.exists()) {
                val username = it.child("userName").value
                val bio = it.child("bio").value
                val profileImage = it.child("profileImageUrl").value
                val realName = it.child("fullName").value

                userProfileView.username_textview.text = username.toString()
                userProfileView.bio_textview.text = bio.toString()
                Picasso.with(activity).load(profileImage.toString())
                    .into(userProfileView.user_photo)
                userProfileView.full_name.text = realName.toString()

            } else {

                Toast.makeText(context, "User does not exist!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()
        }


        tabLayout = userProfileView.findViewById(R.id.profile_tab_layout)
        viewPager = userProfileView.findViewById(R.id.viewpager)
        setupViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_photos)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_videos)
        return userProfileView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        editProfile = view.findViewById(R.id.edit_profile)
        insta = view.findViewById(R.id.insta_logo)
        fb = view.findViewById(R.id.fb_logo)


        if (args.userNumber == phoneNumber) {
            // if this is user profile tab then show edit button
            editProfile.setOnClickListener {
                findNavController().navigate(R.id.action_userProfileFragment_to_updateProfileFragment)
            }
        } else {
            // it is not user's tab
            editProfile.visibility = View.GONE
        }

        insta.setOnClickListener {
            Toast.makeText(context, "Instagram", Toast.LENGTH_SHORT).show()
        }

        fb.setOnClickListener {
            Toast.makeText(context, "Facebook", Toast.LENGTH_SHORT).show()
        }

        profile_back_button.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
        }

        user_photo.setOnClickListener {
            val action =
                UserProfileFragmentDirections.actionUserProfileFragmentToFullscreenImageFragment(
                    args.userNumber
                )
            findNavController().navigate(action)
        }


    }


    private fun setupViewPager(viewpager: ViewPager) {
        val adapter = ViewPagerAdapter((activity as AppCompatActivity).supportFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(GalleryFragment(args.userNumber))
        adapter.addFragment(ClipsFragment(args.userNumber))

        // setting adapter to view pager.
        viewpager.setAdapter(adapter)
    }

    override fun onResume() {
        super.onResume()

        try {

            tabLayout = userProfileView.findViewById(R.id.profile_tab_layout)
            viewPager = userProfileView.findViewById(R.id.viewpager)
            setupViewPager(viewPager)

            tabLayout.setupWithViewPager(viewPager)
            tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_photos)
            tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_videos)

        }catch (e:IllegalStateException){ }
    }
}