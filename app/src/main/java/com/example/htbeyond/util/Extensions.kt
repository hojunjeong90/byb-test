package com.example.htbeyond.util

import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

fun <T : ViewBinding> activityViewBinding(bindingInitializer: (LayoutInflater) -> T): ActivityViewBindingProperty<T> = ActivityViewBindingProperty(bindingInitializer)

fun <T : ViewModel> activityViewModelBinding(modelClass: Class<T>): ViewModelBindingProperty<T> = ViewModelBindingProperty(modelClass)