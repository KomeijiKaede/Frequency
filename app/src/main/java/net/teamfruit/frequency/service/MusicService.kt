package net.teamfruit.frequency.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.teamfruit.frequency.database.MediaMetadataFactory
import net.teamfruit.frequency.util.Extractor
import net.teamfruit.frequency.util.PlayerResponse
import java.net.URLDecoder

class MusicService: MediaBrowserServiceCompat() {
    private lateinit var audioManager: AudioManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat

    private val mediaPlayer = MediaPlayer()
    private val metadataFactory = MediaMetadataFactory(this)
    private val notificationId = 5325
    private var isForegroundService = false

    private val audioManagercallback = AudioManager.OnAudioFocusChangeListener {

    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
            Log.d("MusicService", "Request play from mediaID")

            val gson = Gson()
            Fuel.get("https://www.youtube.com/get_video_info?video_id=$mediaId")
                    .response { _, response, result ->
                        when (result) {
                            is com.github.kittinunf.result.Result.Success -> {
                                val parsedJson: PlayerResponse
                                try {
                                    parsedJson = gson.fromJson(
                                            URLDecoder.decode(Extractor.getStringValueByUrlParameters(
                                                    response.toString(),
                                                    "player_response"),
                                                    "UTF-8"),
                                            PlayerResponse::class.java
                                    )
                                }catch (e: JsonSyntaxException) { return@response }

                                if (parsedJson.playabilityStatus.status == "UNPLAYABLE") return@response

                                val format = Extractor.getBestQualityAudioFormatByAdaptiveFormats(
                                        parsedJson.streamingData.adaptiveFormats) ?: return@response
                                metadataFactory.getMetadata(mediaId)
                                mediaSession.setMetadata(metadataFactory.getMetadata(mediaId))
                                mediaPlayer.apply {
                                    reset()
                                    setDataSource(format.url)
                                    prepare()
                                    start()
                                }
                            }

                            is com.github.kittinunf.result.Result.Failure -> return@response
                        }
                    }
        }

        override fun onPlay() {
            Log.d("MediaSession", "Start")
            mediaPlayer.start()
        }

        override fun onPause() {
            Log.d("MediaSession", "Pause")
            mediaPlayer.pause()
        }

        override fun onSkipToPrevious() {
            Log.d("MusicService", "未実装")
        }

        override fun onSkipToNext() {
            Log.d("MusicService", "未実装")
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
    }
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return MediaBrowserServiceCompat.BrowserRoot("ROOT_ID", null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(MediaMetadataFactory(this).getMediaItems())
    }

    private inner class MediaControllerCallback: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let {
                update()
                updateNotification(it)
            }
            Log.d("Service", "metadata changed")
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { updateNotification(it) }
            Log.d("Service", "playback state changed")
        }

        private fun update() {
            Log.d("Service", "log")
        }
    }

    private fun updateNotification(state: PlaybackStateCompat) {
        val updateState = state.state

        Log.d("Service", "updateNotification")

        if (mediaController.metadata == null) return

        val notification = if (updateState != PlaybackStateCompat.STATE_NONE)
            notificationBuilder.buildNotification(mediaSession.sessionToken) else null

        when (updateState) {
            PlaybackStateCompat.STATE_BUFFERING,
            PlaybackStateCompat.STATE_PLAYING -> {
                Log.d("Service", "STATE_PLAYING")
                if (!isForegroundService) {
                    startService(Intent(applicationContext, this@MusicService::class.java))
                    startForeground(notificationId, notification)
                    isForegroundService = true
                    Log.d("Service", "service start")
                } else if (notification != null) {
                    notificationManager.notify(notificationId, notification)
                    Log.d("Service", "notification")
                }
            }

            else -> {
                Log.d("Service", "ded?")
                if (isForegroundService) {
                    stopForeground(false)
                    isForegroundService = false

                    if (updateState == PlaybackStateCompat.STATE_NONE)
                        stopSelf()

                    if (notification != null)
                        notificationManager.notify(notificationId, notification) else stopForeground(true)
                }
            }
        }
    }
}