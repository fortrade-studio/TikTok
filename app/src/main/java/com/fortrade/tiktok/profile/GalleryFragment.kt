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

        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png",
        "https://cdn1.iconfinder.com/data/icons/ui-colored-1/100/UI__2-512.png"
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