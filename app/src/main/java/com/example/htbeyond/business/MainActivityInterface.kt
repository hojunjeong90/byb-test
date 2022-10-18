package com.example.htbeyond.business

interface MainActivityInterface {
    fun requestBluetoothEnable()
    fun requestPermission() : Boolean
    fun startScan()
    fun stopScan()
}