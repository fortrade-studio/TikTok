package com.fortrade.tiktok.profile.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPagerAdapter : FragmentPagerAdapter {

    // objects of arraylist. One is of Fragment type and
    // another one is of String type.*/
    private final var fragmentList1: ArrayList<Fragment> = ArrayList()

    // this is a secondary constructor of ViewPagerAdapter class.
    public constructor(supportFragmentManager: FragmentManager)
            : super(supportFragmentManager)

    // returns which item is selected from arraylist of fragments.
    override fun getItem(position: Int): Fragment {
        return fragmentList1.get(position)
    }



    // returns the number of items present in arraylist.
    override fun getCount(): Int {
        return fragmentList1.size
    }

    // this function adds the fragment and title in 2 separate  arraylist.
    fun addFragment(fragment: Fragment) {
        fragmentList1.add(fragment)
    }
}
