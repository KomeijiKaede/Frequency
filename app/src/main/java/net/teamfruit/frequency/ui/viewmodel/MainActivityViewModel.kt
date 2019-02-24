package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import net.teamfruit.frequency.service.MusicConnection

class MainActivityViewModel(musicConnection: MusicConnection): ViewModel() {
    val musicConnection = fun(context: Context) {
        musicConnection.also {
            val mediaId = SharedPreferencesManager(context).load() ?: return
            it.subscribe(mediaId, subscriptionCallback)
        }
    }

    val isConnected = musicConnection.isConnected

    private val currentMediaId by lazy {
        musicConnection.rootMediaId
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            Log.d("MediaSession", "Subscribed")
        }
    }

    inner class SharedPreferencesManager(context: Context) {
        private val sharedPreference = context.getSharedPreferences("cache", Context.MODE_PRIVATE)
        fun save() {
            sharedPreference
                    .edit()
                    .putString("mediaId", currentMediaId)
                    .apply()
        }

        fun load(): String? = sharedPreference.getString("mediaId", null)

    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val musicConnection: MusicConnection): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(musicConnection) as T
        }
    }
}