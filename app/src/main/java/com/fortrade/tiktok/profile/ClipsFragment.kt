package com.fortrade.tiktok.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.fortrade.tiktok.R
import com.fortrade.tiktok.profile.Adapter.ClipsAdapter
import com.fortrade.tiktok.profile.Adapter.GalleryAdapter
import kotlinx.android.synthetic.main.fragment_clips.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.recycler_view_item


class ClipsFragment : Fragment() {

    var thumbnails = mutableListOf(

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clips, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val adapter = ClipsAdapter(thumbnails)
        recycler_view_clips.adapter = adapter
        recycler_view_clips.layoutManager = GridLayoutManager(activity,2)


    }


}