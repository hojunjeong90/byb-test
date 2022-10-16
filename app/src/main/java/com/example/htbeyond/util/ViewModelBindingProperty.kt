package com.example.htbeyond.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.htbeyond.Injection
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelBindingProperty<T : ViewModel>(private val modelClass: Class<T>) : ReadOnlyProperty<AppCompatActivity, T> {
    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return ViewModelProvider(thisRef, Injection.provideViewModelFactory(owner = thisRef))[modelClass]
    }
}