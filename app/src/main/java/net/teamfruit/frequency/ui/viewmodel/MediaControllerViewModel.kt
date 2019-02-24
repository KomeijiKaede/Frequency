package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.service.MusicConnection

class MediaControllerViewModel(musicConnection: MusicConnection): ViewModel() {
    private val transportControls = musicConnection.transportControlls

    val state = musicConnection.playbackState

    fun skipToPrev() = transportControls.skipToPrevious()
    fun skipToNext() = transportControls.skipToNext()
    fun playback(state: PlaybackStateCompat) {
        when(state.state) {
            PlaybackStateCompat.STATE_PLAYING -> transportControls.pause()
            PlaybackStateCompat.STATE_PAUSED -> transportControls.play()
            else -> throw IllegalStateException("empty PlaybackState")
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val musicConnection: MusicConnection): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MediaControllerViewModel(musicConnection) as T
        }
    }
}