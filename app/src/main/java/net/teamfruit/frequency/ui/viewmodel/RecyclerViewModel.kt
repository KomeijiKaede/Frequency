package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.*
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.database.DBEntity
import net.teamfruit.frequency.service.MusicConnection
import net.teamfruit.frequency.ui.adapter.RecyclerAdapters

class RecyclerViewModel(private val base: Base, private val musicConnection: MusicConnection): ViewModel(), RecyclerAdapters.OnClickListener<DBEntity> {
    private lateinit var mediaId: String
    private val liveDataList: LiveData<List<DBEntity>> = base.dbdao().liveDataAll()

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            Log.d("MediaSession RecyclerFragment", "Subscribed")
        }
    }

    val adapter = RecyclerAdapters.RecyclerAdapter(arrayListOf(), this)

    fun getList(): LiveData<List<DBEntity>> = liveDataList

    override fun onClick(item: DBEntity) {
        mediaId = item.videoID
        musicConnection.also { it.subscribe(mediaId, subscriptionCallback) }.transportControls.playFromMediaId(mediaId, null)
    }

    override fun onLongClick(item: DBEntity) = base.dbdao().delete(item)

    @Suppress("UNCHECKED_CAST")
    class Factory(private val base: Base,
                  private val musicConnection: MusicConnection): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RecyclerViewModel(base, musicConnection) as T
        }
    }
}