package de.grunert.replyhelper.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.grunert.replyhelper.data.Repository
import de.grunert.replyhelper.data.SettingsStore
import de.grunert.replyhelper.viewmodel.MainViewModel

class AppContainer(context: Context) {
    val settingsStore: SettingsStore = SettingsStore(context)
    val repository: Repository = Repository(settingsStore)
    
    val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


