package net.teamfruit.frequency.ui.layout

import android.graphics.Color
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.gridlayout.R.id.vertical
import net.teamfruit.frequency.MainActivity
import net.teamfruit.frequency.R
import net.teamfruit.frequency.util.Page
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager

class MainActivityUI: AnkoComponent<MainActivity> {
    private lateinit var viewPager: ViewPager
    private val backgroundColorGray = Color.rgb(48,48,48)

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        constraintLayout {
            verticalLayout {
                viewPager = viewPager {
                    id = R.id.view_pager
                }.lparams {
                    weight = 1F
                }
                verticalLayout {
                    id = R.id.media_controller //Attach fragments later
                    backgroundColor = backgroundColorGray
                }.lparams(width = matchParent, height = dip(50))
                tabLayout {
                    id = R.id.tab
                    setupWithViewPager(viewPager)
                    backgroundColor = backgroundColorGray
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams {
                orientation = vertical
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