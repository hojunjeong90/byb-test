package com.example.htbeyond.util

import android.view.LayoutInflater
import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle.Event.*
import androidx.viewbinding.ViewBinding

open class ViewBindingProperty<T : ViewBinding>(
    protected val bindingInitializer: (LayoutInflater) -> T
) : LifecycleEventObserver {

    protected var binding: T? = null
    protected var lifecycle: Lifecycle? = null

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == ON_DESTROY){
            lifecycle?.removeObserver(this)
            lifecycle = null
            binding = null
        }
    }
}