package net.teamfruit.frequency.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_browser.*
import kotlinx.android.synthetic.main.fragment_mediacontroller.*
import net.teamfruit.frequency.R
import net.teamfruit.frequency.ui.viewmodel.MediaControllerViewModel
import net.teamfruit.frequency.util.EMPTY_PLAYBACK_STATE
import net.teamfruit.frequency.util.Injector

class MediaControllerFragment: Fragment() {
    private lateinit var viewModel: MediaControllerViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders
                .of(this, Injector.provideMediaControllerFragment(context))
                .get(MediaControllerViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mediacontroller, container, false)
    }

    override fun onResume() {
        super.onResume()

        button_prev.setOnClickListener {
            viewModel.skipToPrev()
        }

        button_next.setOnClickListener {
            viewModel.skipToNext()
        }

        button_playback.setOnClickListener {
            viewModel.state.observe(this, Observer { state ->
                viewModel.playback(state?: EMPTY_PLAYBACK_STATE)
            })

        }
    }
}