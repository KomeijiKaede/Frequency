package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.teamfruit.frequency.service.MusicConnection

class MainActivityViewModel(musicConnection: MusicConnection): ViewModel() {
    val state = musicConnection.playbackState

    @Suppress("UNCHECKED_CAST")
    class Factory(private val musicConnection: MusicConnection): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(musicConnection) as T
        }
    }
}