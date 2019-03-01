package net.teamfruit.frequency.database

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.bumptech.glide.Glide
import net.teamfruit.frequency.util.Injector
import net.teamfruit.frequency.util.music

class MediaMetadataFactory(private val context: Context) {
    private val base = Injector.provideBase(context)

    init { for (item in base.dbdao().findAll()) create(item.videoID, item.title, item.thumbnail) }

    fun getMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        val list: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
        for (metadata in music.values)
            list.add(MediaBrowserCompat.MediaItem(metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
        return list
    }

    fun getMetadata(mediaId: String): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,
                        music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getBitmap(mediaId))
                .build()
    }

    private fun getBitmap(mediaId: String) = Glide.with(context)
            .asBitmap()
            .load(music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
            .submit(300,300)
            .get()

    private fun create(mediaId: String, title: String, thumbnail: String) {
        music[mediaId] = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, thumbnail)
                .build()
    }
}