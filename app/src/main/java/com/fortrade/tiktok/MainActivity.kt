package com.fortrade.tiktok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.fortrade.tiktok.authentication.AuthFragment
import com.fortrade.tiktok.profile.ClipsFragment
import com.fortrade.tiktok.profile.GalleryFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

    }



    override fun onStart() {
        super.onStart()

        if (auth.currentUser==null){
            // then we will navigate somewhere else
        }else{
            Toast.makeText(this,R.string.signed_in,Toast.LENGTH_SHORT).show()
        }
    }
}