package com.example.jobpa.finalproject

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager){

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(p0: Int): Fragment {
        return when (p0) {
            0 -> Booking.newInstance()
            1 -> Login.newInstance()
            2 -> Register.newInstance()
            else -> Booking.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val name = arrayOf("Booking","Login","Register")
        return name[position]
    }
}