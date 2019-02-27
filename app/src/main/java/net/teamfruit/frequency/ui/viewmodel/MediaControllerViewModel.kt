package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.service.MusicConnection

class MediaControllerViewModel(musicConnection: MusicConnection): ViewModel() {
    private val musicConnection = musicConnection.also {
        it.subscribe(it.rootMediaId, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                super.onChildrenLoaded(parentId, children)
                Log.d("MediaControllerFragment", "Subscribed")
            }
        })
    }

    val transportControls by lazy {
        this.musicConnection.transportControls
    }

    val state = this.musicConnection.state

    fun skipToPrev() = transportControls.skipToPrevious()
    fun skipToNext() = transportControls.skipToNext()

    @Suppress("UNCHECKED_CAST")
    class Factory(private val musicConnection: MusicConnection): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MediaControllerViewModel(musicConnection) as T
        }
    }
}