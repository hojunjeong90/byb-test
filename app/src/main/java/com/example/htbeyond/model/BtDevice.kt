package com.example.htbeyond.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BtDevice(
    val deviceName: String?,
    val deviceAddress: String,
    val isConnectable: Boolean,
    val timestamp: Long,
    val bluetoothDevice: BluetoothDevice
) : Parcelable {

    companion object {
        @SuppressLint("MissingPermission")
        fun setBluetoothDeviceToBtDevice(bluetoothDevice: BluetoothDevice): BtDevice {
            return BtDevice(
                bluetoothDevice.name,
                bluetoothDevice.address,
                true,
                System.currentTimeMillis(),
                bluetoothDevice
            )
        }
    }
}
