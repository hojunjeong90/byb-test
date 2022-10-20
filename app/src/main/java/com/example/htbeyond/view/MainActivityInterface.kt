package com.example.htbeyond.view

interface MainActivityInterface {
    fun requestBluetoothEnable()
    fun requestPermission() : Boolean
    fun startScan()
    fun stopScan()
}