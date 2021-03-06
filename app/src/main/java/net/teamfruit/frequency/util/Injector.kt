package net.teamfruit.frequency.util

import android.content.ComponentName
import android.content.Context
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.service.MusicConnection
import net.teamfruit.frequency.service.MusicService
import net.teamfruit.frequency.ui.viewmodel.BrowserViewModel
import net.teamfruit.frequency.ui.viewmodel.MainActivityViewModel
import net.teamfruit.frequency.ui.viewmodel.MediaControllerViewModel
import net.teamfruit.frequency.ui.viewmodel.RecyclerViewModel

object Injector {
    private fun provideMusicConnection(context: Context) : MusicConnection {
        return MusicConnection.getInstance(context, ComponentName(context, MusicService::class.java))
    }

    fun provideBase(context: Context) = Base.getDataBase(context)

    fun provideRecyclerFragment(context: Context) : RecyclerViewModel.Factory {
        val base = provideBase(context)
        val appContext = context.applicationContext
        val musicConnection = provideMusicConnection(appContext)
        return RecyclerViewModel.Factory(base, musicConnection)
    }

    fun provideBrowserFragment(context: Context) : BrowserViewModel.Factory {
        return BrowserViewModel.Factory(provideBase(context), provideMusicConnection(context.applicationContext))
    }

    fun provideMediaControllerFragment(context: Context) : MediaControllerViewModel.Factory {
        val appContext = context.applicationContext
        val musicConnection = provideMusicConnection(appContext)
        return MediaControllerViewModel.Factory(musicConnection)
    }

    fun provideMainActivity(context: Context) : MainActivityViewModel.Factory {
        val appContext = context.applicationContext
        val musicConnection = provideMusicConnection(appContext)
        return MainActivityViewModel.Factory(musicConnection)
    }
}