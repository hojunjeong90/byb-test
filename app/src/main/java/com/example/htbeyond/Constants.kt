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
            FOREGROUND_SERVICE
        )
    } else {
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            FOREGROUND_SERVICE
        )
    }

    //사용자 BLE UUID Service/Rx/Tx
    const val SERVICE_STRING = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
    const val CHARACTERISTIC_COMMAND_STRING = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
    const val CHARACTERISTIC_RESPONSE_STRING = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"
    //BluetoothGattDescriptor 고정
    const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"

    object IntentAction {
        private const val prefix = "hojun.htbeyond.intent.action:"
        const val START_SCAN_FOREGROUND = prefix + "startForegroundForScan"
        const val STOP_SCAN_FOREGROUND = prefix + "stopForegroundForScan"
        const val SCAN_BT_DEVICE = prefix + "foundBleDevice"

        const val START_GATT_FOREGROUND = prefix + "startForegroundForGatt"
        const val STOP_GATT_FOREGROUND = prefix + "stopForegroundForGatt"
        const val GATT_CONNECTED = prefix + "gattconnected"
        const val GATT_DISCONNECTED = prefix + "gattdisconnected"
        const val STATUS_MSG = prefix + "statusmsg"

        const val DISCONNECT_DEVICE = prefix + "disconnectdevice"
        const val CONNECT_DEVICE = prefix + "disconnectdevice"
    }

    object IntentKey {
        private const val prefix = "hojun.htbeyond.intent.key:"
        const val BT_DEVICE = prefix + "BleDevice"
        const val BLUETOOTH_DEVICE = prefix + "BluetoothDevice"
        const val MSG_DATA = prefix + "msgdata"
    }

    object TestLatLng {
         const val Lat = 37.5143
         const val Lng = 127.1202
    }
}