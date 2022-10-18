package com.example.htbeyond

import android.Manifest.permission.*
import android.os.Build

object Constants {

    val PERMISSIONS = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            ACCESS_BACKGROUND_LOCATION,
            BLUETOOTH,
            BLUETOOTH_ADMIN,
        )
    } else {
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            BLUETOOTH,
            BLUETOOTH_ADMIN,
        )
    }

    const val ACTION_GATT_CONNECTED = "com.lilly.ble.ACTION_GATT_CONNECTED"
    const val ACTION_GATT_DISCONNECTED = "com.lilly.ble.ACTION_GATT_DISCONNECTED"
    const val ACTION_STATUS_MSG = "com.lilly.ble.ACTION_STATUS_MSG"
    const val ACTION_READ_DATA= "com.lilly.ble.ACTION_READ_DATA"
    const val EXTRA_DATA = "com.lilly.ble.EXTRA_DATA"
    const val MSG_DATA = "com.lilly.ble.MSG_DATA"
    // used to identify adding bluetooth names
    const val REQUEST_ENABLE_BT = 1
    // used to request fine location permission
    const val REQUEST_ALL_PERMISSION = 2

    //사용자 BLE UUID Service/Rx/Tx
    const val SERVICE_STRING = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
    const val CHARACTERISTIC_COMMAND_STRING = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
    const val CHARACTERISTIC_RESPONSE_STRING = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

    //BluetoothGattDescriptor 고정
    const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"

    object Actions {
        private const val prefix = "lilly.ble.mvvmservice"
        const val START_FOREGROUND = prefix + "startforeground"
        const val STOP_FOREGROUND = prefix + "stopforeground"
        const val DISCONNECT_DEVICE = prefix + "disconnectdevice"
        const val CONNECT_DEVICE = prefix + "disconnectdevice"
        const val START_NOTIFICATION = prefix + "startnotification"
        const val STOP_NOTIFICATION = prefix + "stopnotification"
        const val WRITE_DATA = prefix + "writedata"
        const val READ_CHARACTERISTIC= prefix + "readcharacteristic"
        const val READ_BYTES = prefix + "readbytes"
        const val GATT_CONNECTED = prefix + "gattconnected"
        const val GATT_DISCONNECTED = prefix + "gattdisconnected"
        const val STATUS_MSG = prefix + "statusmsg"
        const val MSG_DATA = prefix + "msgdata"
    }
}