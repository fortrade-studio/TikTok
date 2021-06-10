package com.fortrade.tiktok.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryFragment : Fragment() {

    var images = mutableListOf(
        "http://aryeahtyagi.herokuapp.com/me.jpg",
        "https://instagram.flko4-1.fna.fbcdn.net/v/t51.2885-15/sh0.08/e35/s750x750/69285147_719829075178685_2840810630290609170_n.jpg?tp=1&_nc_ht=instagram.flko4-1.fna.fbcdn.net&_nc_cat=105&_nc_ohc=yEZ1gcsyqgAAX8MzEon&edm=AP_V10EBAAAA&ccb=7-4&oh=dc4ca0b27c7e7c82844f3de0da72b2fe&oe=60C9555E&_nc_sid=4f375e",
        "https://instagram.flko4-1.fna.fbcdn.net/v/t51.2885-15/e35/c0.107.1206.1206a/s240x240/67500039_741688716290675_5800233088515666625_n.jpg?tp=1&_nc_ht=instagram.flko4-1.fna.fbcdn.net&_nc_cat=111&_nc_ohc=DVK4mteB_OkAX-oBC_T&edm=ABfd0MgBAAAA&ccb=7-4&oh=9afea6f68aea5fc31ffb28986c2b34ea&oe=60C829F9&_nc_sid=7bff83"
    )

    val adapter = GalleryAdapter(images)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_gallery, container, false)



        return view
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)


        recycler_view_item.adapter = adapter
        recycler_view_item.layoutManager = GridLayoutManager(activity,3)


    }

}