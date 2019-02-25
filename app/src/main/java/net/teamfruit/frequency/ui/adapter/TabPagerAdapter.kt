package net.teamfruit.frequency.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import net.teamfruit.frequency.ui.fragment.BrowserFragment
import net.teamfruit.frequency.ui.fragment.RecyclerFragment

class TabPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getCount() = 2

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> RecyclerFragment()
            1 -> BrowserFragment()
            else -> RecyclerFragment()
        }
    }
}