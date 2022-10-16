package com.example.htbeyond.util

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBindingProperty<T : ViewBinding>(
    bindingInitializer: (LayoutInflater) -> T
) : ViewBindingProperty<T>(bindingInitializer), ReadOnlyProperty<AppCompatActivity, T> {

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T =
        binding ?: run {
            thisRef.lifecycle
                .apply {
                    addObserver(this@ActivityViewBindingProperty)
                }.let {
                    lifecycle = it
                }
            bindingInitializer.invoke(thisRef.layoutInflater)
                .also {
                    binding = it
                }
        }

}