package net.teamfruit.frequency.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.database.DBEntity
import net.teamfruit.frequency.util.Extractor

class BrowserViewModel(private val base: Base) : ViewModel() {
    fun getBase() = base

    @Suppress("UNCHECKED_CAST")
    class Factory(private val base: Base) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BrowserViewModel(base) as T
        }
    }
}