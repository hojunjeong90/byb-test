package com.example.htbeyond

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.htbeyond.viewmodel.MainViewModel

object Injection {

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner)
    }

    @Suppress("UNCHECKED_CAST")
    private class ViewModelFactory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


