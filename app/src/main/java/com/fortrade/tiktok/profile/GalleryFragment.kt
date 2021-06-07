package com.fortrade.tiktok.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fortrade.tiktok.R
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryFragment : Fragment() {

    var images = mutableListOf(

        R.drawable.white_person_icon,
        R.drawable.pp,
        R.drawable.pp,
        R.drawable.ic_images,
        R.drawable.ic_images,
        R.drawable.ic_images,
        R.drawable.pp,
        R.drawable.pp

    )


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

        val adapter = GalleryAdapter(images)
        recycler_view_item.adapter = adapter
        recycler_view_item.layoutManager = GridLayoutManager(activity,3)


    }
}