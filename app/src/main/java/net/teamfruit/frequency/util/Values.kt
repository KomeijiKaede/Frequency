package net.teamfruit.frequency.util

import android.support.v4.app.Fragment
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import net.teamfruit.frequency.ui.fragment.BrowserFragment
import net.teamfruit.frequency.ui.fragment.RecyclerFragment
import java.util.*

data class Page(val title: String, val fragment: Fragment)

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()

const val CHANNEL_ID = "net.teamfruit.frequency"
const val NOTIFICATION_ID = 5613
const val APPLICATION_NAME = "Frequency"

val music = TreeMap<String, MediaMetadataCompat>()

val pages = listOf(Page("home", RecyclerFragment()),Page("browser", BrowserFragment()))