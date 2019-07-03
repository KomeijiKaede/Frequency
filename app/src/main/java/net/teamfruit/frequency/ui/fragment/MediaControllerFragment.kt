package net.teamfruit.frequency.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_mediacontroller.*
import net.teamfruit.frequency.R
import net.teamfruit.frequency.ui.viewmodel.MediaControllerViewModel
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

    override fun onStart() {
        super.onStart()

        button_prev.setOnClickListener { viewModel.transportControls.skipToPrevious() }

        button_next.setOnClickListener { viewModel.transportControls.skipToNext() }

        button_playback.setOnClickListener {
            when (viewModel.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    viewModel.transportControls.pause()
                    button_playback.setImageResource(R.drawable.exo_controls_pause)
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    viewModel.transportControls.play()
                    button_playback.setImageResource(R.drawable.exo_controls_play)
                }
            }
        }
    }
}