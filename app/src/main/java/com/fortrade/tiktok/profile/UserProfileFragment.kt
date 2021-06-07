package com.fortrade.tiktok.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.fortrade.tiktok.R
import com.fortrade.tiktok.profile.Adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_user_profile.*


class UserProfileFragment : Fragment() {

    lateinit var tabLayout:TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.getSupportActionBar()?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.profile_tab_layout)
        viewPager = view.findViewById(R.id.viewpager)
        setupViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)

        getUserData()
        
    }

    private fun getUserData() {

        database = FirebaseDatabase.getInstance().getReference("userProfileData")

        database.
        database.get().addOnSuccessListener {

            if (it.exists()){

                val fullName = it.child("fullName").value
                val userName = it.child("userName").value
                val bio = it.child("bio").value
                Toast.makeText(activity,"Successfuly Read",Toast.LENGTH_SHORT).show()
                username_textview.setText(userName.toString())
                bio_textview.setText(bio.toString())


            }else{

                Toast.makeText(activity,"User Doesn't Exist",Toast.LENGTH_SHORT).show()


            }

        }.addOnFailureListener{

            Toast.makeText(activity,"Failed",Toast.LENGTH_SHORT).show()


        }

    }


    private fun setupViewPager(viewpager: ViewPager) {
        var adapter = ViewPagerAdapter((activity as AppCompatActivity).supportFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(GalleryFragment(),"Gallery")
        adapter.addFragment(ClipsFragment(), "CLips")

        // setting adapter to view pager.
        viewpager.setAdapter(adapter)
    }



}