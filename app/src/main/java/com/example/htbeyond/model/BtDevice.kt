package com.example.htbeyond.model

data class BtDevice(
    val deviceName: String?,
    val deviceAddress: String,
    val isConnectable: Boolean,
    val timestamp: Long,
    val txPower: Int,
    val interval: Int
)
