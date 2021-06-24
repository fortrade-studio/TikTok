package com.fortrade.tiktok.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.fortrade.tiktok.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_fullscreen_image.view.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*


class FullscreenImageFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private val args by navArgs<FullscreenImageFragmentArgs>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false)

        database = FirebaseDatabase.getInstance().getReference("userProfileData")
        database.child(args.userNumber).get().addOnSuccessListener{


            if (it.exists()) {
                val profileImage = it.child("profileImageUrl").value


                Picasso.with(activity).load(profileImage.toString()).into(view.image_fullview)

            } else {

                Toast.makeText(context, "Unable to open the image!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()
        }
        return view
    }

}