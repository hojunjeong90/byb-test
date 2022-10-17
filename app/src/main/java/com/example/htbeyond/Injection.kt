package com.example.htbeyond

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.htbeyond.viewmodel.MainViewModel

object Injection {

    fun provideViewModelFactory(application: Application, owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(application, owner)
    }


    @Suppress("UNCHECKED_CAST")
    private class ViewModelFactory(val application: Application, owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


