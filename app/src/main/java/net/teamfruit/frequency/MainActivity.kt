package net.teamfruit.frequency

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import net.teamfruit.frequency.ui.adapter.TabPagerAdapter
import net.teamfruit.frequency.ui.fragment.MediaControllerFragment
import net.teamfruit.frequency.ui.viewmodel.MainActivityViewModel
import net.teamfruit.frequency.util.Injector

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders
                .of(this, Injector.provideMainActivity(this))
                .get(MainActivityViewModel::class.java)

        viewModel.musicConnection(this)

        viewModel.isConnected.observe(this, Observer {
            if (it!!) supportFragmentManager.beginTransaction()
                    .add(R.id.media_controller, MediaControllerFragment())
                    .commit() })

        viewPager.adapter = TabPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }
}