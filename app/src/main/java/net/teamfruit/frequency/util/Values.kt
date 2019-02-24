package net.teamfruit.frequency.util

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()

val CHANNEL_ID = "net.teamfruit.frequency"