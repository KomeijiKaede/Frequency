package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import com.google.api.services.youtube.model.SearchResult
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.database.MetadataFactory
import net.teamfruit.frequency.service.MusicConnection
import net.teamfruit.frequency.ui.adapter.RecyclerAdapters
import net.teamfruit.frequency.util.AddAsyncTask
import net.teamfruit.frequency.util.DataAPIAccess
import kotlin.concurrent.thread

class BrowserViewModel(private val base: Base, private val musicConnection: MusicConnection) : ViewModel(), RecyclerAdapters.OnClickListener<SearchResult> {
    private val dataAPIAccess = DataAPIAccess()
    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            Log.d("MediaSession RecyclerFragment", "Subscribed")
        }
    }

    val searchResult = MutableLiveData<List<SearchResult>>()

    fun search(query: String) {
        searchResult.postValue(arrayListOf())
        thread { searchResult.postValue(dataAPIAccess.search(query)) }
    }

    val adapter = RecyclerAdapters.BrowserAdapter(arrayListOf(), this)

    override fun onClick(item: SearchResult) {
        val mediaId = item.id.videoId
        MetadataFactory.create(mediaId, item.snippet.title, item.snippet.thumbnails.high.url)
        musicConnection.also { it.subscribe(mediaId, subscriptionCallback) }.transportControls.playFromMediaId(mediaId, null)
        Log.d("Browser", mediaId)
    }

    override fun onLongClick(item: SearchResult) {
        AddAsyncTask(base).execute(Base.create(item.snippet.title, item.id.videoId, item.snippet.thumbnails.high.url))
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val base: Base,
                  private val musicConnection: MusicConnection) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BrowserViewModel(base, musicConnection) as T
        }
    }
}