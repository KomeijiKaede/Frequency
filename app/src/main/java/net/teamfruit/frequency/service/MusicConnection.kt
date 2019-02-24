package net.teamfruit.frequency.service

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import net.teamfruit.frequency.util.EMPTY_PLAYBACK_STATE
import net.teamfruit.frequency.util.NOTHING_PLAYING

class MusicConnection(context: Context, serviceComponent: ComponentName) {
    val rootMediaId: String get() = mediaBrowser.root

    val isConnected = MutableLiveData<Boolean>()
            .apply { postValue(false) }
    val playbackState = MutableLiveData<PlaybackStateCompat>()
            .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
            .apply { postValue(NOTHING_PLAYING) }

    val transportControlls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls ?: throw IllegalStateException("mediaController is null")

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) =
        mediaBrowser.subscribe(parentId, callback)
    fun unsubscribe(parentId: String) =
        mediaBrowser.unsubscribe(parentId)

    private lateinit var mediaController: MediaControllerCompat
    private val callback = ConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(context, serviceComponent, callback, null)
            .apply { connect() }


    inner class ConnectionCallback(private val context: Context) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(object : MediaControllerCompat.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) =
                            this@MusicConnection.playbackState.postValue(state?: EMPTY_PLAYBACK_STATE)
                    override fun onMetadataChanged(metadata: MediaMetadataCompat?) =
                            this@MusicConnection.nowPlaying.postValue(metadata?: NOTHING_PLAYING)
                }) }
            isConnected.postValue(true)
        }

        override fun onConnectionFailed() = isConnected.postValue(false)
        override fun onConnectionSuspended() = isConnected.postValue(false)
    }

    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: MusicConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
                instance ?: synchronized(this) {
                    instance ?: MusicConnection(context, serviceComponent)
                            .also { instance = it }
                }
    }
}