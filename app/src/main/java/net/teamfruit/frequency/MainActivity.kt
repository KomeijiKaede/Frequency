package net.teamfruit.frequency

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import net.teamfruit.frequency.ui.fragment.*
import net.teamfruit.frequency.ui.layout.MainActivityUI
import net.teamfruit.frequency.ui.viewmodel.MainActivityViewModel
import net.teamfruit.frequency.util.Injector
import net.teamfruit.frequency.util.pages
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders
                .of(this, Injector.provideMainActivity(this))
                .get(MainActivityViewModel::class.java)

        MainActivityUI().apply {
            setContentView(this@MainActivity)
            showPages(supportFragmentManager, pages)
            viewModel.state.observe(this@MainActivity, Observer {
                if (it!!.state == PlaybackStateCompat.STATE_PLAYING) {
                    supportFragmentManager.beginTransaction()
                            .add(R.id.media_controller, MediaControllerFragment())
                            .commit()
                    Log.d("MainActivity", "Fragment attached")
                }
            })
        }
    }
}