package net.teamfruit.frequency.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import net.teamfruit.frequency.util.Injector
import net.teamfruit.frequency.util.music
import java.io.IOException
import java.io.InputStream
import java.net.URL

class MediaMetadataFactory(context: Context) {
    private val base = Injector.provideBase(context)

    fun convert() {
        for (item in base.dbdao().findAll()) create(item.videoID, item.title, item.thumbnail)
        Log.d("Factory", "convert")
    }

    fun getMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        val list : MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
        for (metadata in music.values)
            list.add(MediaBrowserCompat.MediaItem(metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
        return list
    }

    fun getMetadata(mediaId: String) : MediaMetadataCompat {
        val builder = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
        Fuel.get(music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
                .response { _, response, result ->
                    when (result) {
                        is Result.Success -> {
                            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeStream(response.dataStream))
                        }
                        is Result.Failure -> return@response
                    }
                }
        return builder.build()
    }

    private fun create(mediaId: String, title: String, thumbnail: String) {
        music[mediaId] = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, thumbnail)
                .build()
        Log.d("Factory", music[mediaId]!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
    }
}