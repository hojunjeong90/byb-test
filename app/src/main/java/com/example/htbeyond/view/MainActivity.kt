package com.example.htbeyond.view

import com.example.htbeyond.databinding.ActivityMainBinding
import com.example.htbeyond.util.BaseActivity
import com.example.htbeyond.util.activityViewBinding
import com.example.htbeyond.util.activityViewModelBinding
import com.example.htbeyond.viewmodel.MainViewModel

class MainActivity : BaseActivity() {

    override val binding by activityViewBinding(ActivityMainBinding::inflate)

    override val viewModel by activityViewModelBinding(MainViewModel::class.java)

}