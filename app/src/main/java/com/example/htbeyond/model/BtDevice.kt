package com.example.htbeyond.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BtDevice (
    val deviceName: String?,
    val deviceAddress: String,
    val isConnectable: Boolean,
    val timestamp: Long,
    val txPower: Int,
    val interval: Int
) : Parcelable
