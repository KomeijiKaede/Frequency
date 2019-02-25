package net.teamfruit.frequency.database

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import net.teamfruit.frequency.util.Injector
import net.teamfruit.frequency.util.music

class MediaMetadataFactory(context: Context) {
    private val base = Injector.provideBase(context)

    fun convert() {
        for (item in base.dbdao().findAll()) create(item.videoID, item.title)
        Log.d("Factory", "convert")
    }

    fun getMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        val list : MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
        for (metadata in music.values)
            list.add(MediaBrowserCompat.MediaItem(metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
        return list
    }

    fun getMetadata(mediaId: String) : MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .build()
    }

    private fun create(mediaId: String, title : String) {
        music[mediaId] = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .build()
        Log.d("Factory", music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
    }
}