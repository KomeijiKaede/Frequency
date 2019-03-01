package net.teamfruit.frequency.ui.fragment

import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import net.teamfruit.frequency.MainActivity
import net.teamfruit.frequency.R
import net.teamfruit.frequency.util.Page
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager


class MainActivityUI: AnkoComponent<MainActivity> {
    private lateinit var viewPager: ViewPager
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        constraintLayout {
            verticalLayout {
                val tabLayout = tabLayout {
                    tabGravity = TabLayout.GRAVITY_CENTER
                }.lparams(width = matchParent, height = wrapContent)

                viewPager = viewPager {
                    id = R.id.view_pager
                }.lparams(width = matchParent, height = wrapContent)
                    tabLayout.setupWithViewPager(viewPager)
                }
            verticalLayout {
                id = R.id.media_controller
                //minimumHeight = 0

                }.layoutParams.apply {
                width = matchParent
                height = 80
            }
        }
    }

    fun showPages(fragmentManager: FragmentManager, pages: List<Page>) {
        viewPager.adapter = PagerAdapter(fragmentManager, pages)
    }

    inner class PagerAdapter(fragmentManager: FragmentManager, private val pages: List<Page>): FragmentPagerAdapter(fragmentManager) {
        override fun getCount() = pages.size
        override fun getItem(position: Int) = pages[position].fragment
        override fun getPageTitle(position: Int) = pages[position].title
    }
}