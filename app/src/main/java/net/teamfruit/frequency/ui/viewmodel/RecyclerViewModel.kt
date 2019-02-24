package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.database.DBEntity
import net.teamfruit.frequency.service.MusicConnection
import net.teamfruit.frequency.ui.adapter.RecyclerAdapter
import net.teamfruit.frequency.util.AddAsyncTask

class RecyclerViewModel(private val base: Base, private val musicConnection: MusicConnection): ViewModel(), RecyclerAdapter.OnClickListener {
    private lateinit var mediaId: String
    private val liveDataList: LiveData<List<DBEntity>> = base.dbdao().livedataAll()

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            Log.d("MediaSession", "Subscribed")
        }
    }

    val adapter = RecyclerAdapter(arrayListOf(), this)

    fun getList(): LiveData<List<DBEntity>> = liveDataList

    override fun onClick(entity: DBEntity) {
        mediaId = entity.videoID
        musicConnection.also { it.subscribe(mediaId, subscriptionCallback) }.transportControlls.playFromMediaId(mediaId, null)
    }

    override fun onLongClick(entity: DBEntity) = base.dbdao().delete(entity)

    @Suppress("UNCHECKED_CAST")
    class Factory(private val base: Base,
                  private val musicConnection: MusicConnection): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RecyclerViewModel(base, musicConnection) as T
        }
    }
}