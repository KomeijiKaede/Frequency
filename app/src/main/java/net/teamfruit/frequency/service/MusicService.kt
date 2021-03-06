package net.teamfruit.frequency.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.github.kittinunf.fuel.Fuel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.teamfruit.frequency.database.MediaMetadataFactory
import net.teamfruit.frequency.util.*

class MusicService: MediaBrowserServiceCompat() {
    private lateinit var audioManager: AudioManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat

    private var isForegroundService = false

    private var index = 0 //This is currently selected playlist index

    private val queueList = mutableListOf<MediaSessionCompat.QueueItem>()
    private val metadataFactory = MediaMetadataFactory(this)

    private val attribute = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    private val exoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(attribute, false)
            addListener(stateListener)
        }
    }

    private val audioManagercallback = AudioManager.OnAudioFocusChangeListener {

    }

    private val stateListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) = updateState()
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
            val gson = Gson()
            Fuel.get(awsurl(mediaId))
                    .response { _, response, result ->
                        when (result) {
                            is com.github.kittinunf.result.Result.Success -> {
                                val parsedJson: ResponseJson
                                val body = response.toString().split("\n")
                                try {
                                    parsedJson = gson.fromJson(
                                            body[3].substring(7), ResponseJson::class.java
                                    )
                                } catch (e: JsonSyntaxException) { return@response }

                                val dataSourceFactory = DefaultHttpDataSourceFactory(
                                        Util.getUserAgent(this@MusicService, APPLICATION_NAME))

                                val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(Uri.parse(parsedJson.body.url.url))

                                exoPlayer.prepare(mediaSource)

                                Thread { mediaSession.setMetadata(metadataFactory.getMetadata(mediaId))}.start()

                                onPlay()
                            }

                            is com.github.kittinunf.result.Result.Failure -> return@response
                        }
                    }
        }

        override fun onPlay() {
            exoPlayer.playWhenReady = true
        }

        override fun onPause() {
            exoPlayer.playWhenReady = false
        }

        override fun onSkipToPrevious() {
            index--
            if (index < queueList.size) index = queueList.lastIndex
            onPlayFromMediaId(queueList[index].description.mediaId ?: throw IllegalStateException(), null)
        }

        override fun onSkipToNext() {
            index++
            if (index >= queueList.size) index = 0
            onPlayFromMediaId(queueList[index].description.mediaId ?: throw IllegalStateException(), null)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            exoPlayer.repeatMode = repeatMode
        }
    }

    override fun onCreate() {
        super.onCreate()
        val sessionIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(this, 0, sessionIntent, 0)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mediaSession = MediaSessionCompat(this, "SESSION_TAG").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(mediaSessionCallback)
            setSessionActivity(sessionActivityPendingIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken
        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback())
        }

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)

        for ((x, item) in metadataFactory.getMediaItems().withIndex())
            queueList.add(MediaSessionCompat.QueueItem(item.description, x.toLong()))

        mediaSession.setQueue(queueList)
    }
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return MediaBrowserServiceCompat.BrowserRoot("ROOT_ID", null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(metadataFactory.getMediaItems())
    }

    private fun updateState() {
        val state = when(exoPlayer.playbackState) {
            Player.STATE_IDLE -> PlaybackStateCompat.STATE_NONE
            Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
            Player.STATE_READY -> if (exoPlayer.playWhenReady)
                PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            Player.STATE_ENDED -> PlaybackStateCompat.STATE_STOPPED
            else -> throw IllegalStateException()
        }

        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                .setState(state, exoPlayer.contentPosition, exoPlayer.playbackParameters.speed)
                .build())
    }

    private inner class MediaControllerCallback: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let { updateNotification(it) }
            for ((x, item) in metadataFactory.getMediaItems().withIndex()) {
                queueList.add(MediaSessionCompat.QueueItem(item.description, x.toLong()))
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { updateNotification(it) }
        }

        private fun updateNotification(state: PlaybackStateCompat) {
            val updateState = state.state

            if (mediaController.metadata == null) return

            val notification = if (updateState != PlaybackStateCompat.STATE_NONE)
                notificationBuilder.buildNotification(mediaSession.sessionToken) else null

            when (updateState) {
                PlaybackStateCompat.STATE_BUFFERING,
                PlaybackStateCompat.STATE_PLAYING -> {
                    if (!isForegroundService) {
                        startService(Intent(applicationContext, this@MusicService::class.java))
                        startForeground(NOTIFICATION_ID, notification)
                        isForegroundService = true
                    } else if (notification != null)
                        notificationManager.notify(NOTIFICATION_ID, notification)
                }

                else -> {
                    if (isForegroundService) {
                        stopForeground(false)
                        isForegroundService = false

                        if (updateState == PlaybackStateCompat.STATE_NONE) stopSelf()

                        if (notification != null) notificationManager.notify(NOTIFICATION_ID, notification)
                        else stopForeground(true)
                    }
                }
            }
        }
    }
}