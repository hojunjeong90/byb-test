package com.example.htbeyond.business

import android.bluetooth.BluetoothAdapter

interface MainActivityInterface {
    fun requestBluetoothEnable()
    fun requestPermission()
    fun startScan()
    fun stopScan()
}