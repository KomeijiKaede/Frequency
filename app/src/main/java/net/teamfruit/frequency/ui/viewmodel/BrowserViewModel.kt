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
import net.teamfruit.frequency.ui.adapter.BrowserAdapter
import net.teamfruit.frequency.util.AddAsyncTask
import net.teamfruit.frequency.util.DataAPIAccess
import kotlin.concurrent.thread

class BrowserViewModel(private val base: Base, private val musicConnection: MusicConnection) : ViewModel(), BrowserAdapter.OnClickListener {
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

    val adapter = BrowserAdapter(arrayListOf(), this)

    override fun onClick(res: SearchResult) {
        val mediaId = res.id.videoId
        MetadataFactory.create(mediaId, res.snippet.title, res.snippet.thumbnails.high.url)
        Log.d("Browser", mediaId)
        musicConnection.also { it.subscribe(mediaId, subscriptionCallback) }.transportControls.playFromMediaId(mediaId, null)
    }

    override fun onLongClick(res: SearchResult) {
        AddAsyncTask(base).execute(Base.create(res.snippet.title, res.id.videoId, res.snippet.thumbnails.high.url))
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val base: Base,
                  private val musicConnection: MusicConnection) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BrowserViewModel(base, musicConnection) as T
        }
    }
}