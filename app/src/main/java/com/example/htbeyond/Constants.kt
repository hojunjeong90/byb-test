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

    object IntentAction {
        private const val prefix = "hojun.htbeyond.intent.action:"
        const val START_FOREGROUND = prefix + "startForegroundForScan"
        const val STOP_FOREGROUND = prefix + "stopForegroundForScan"
        const val FOUND_BT_DEVICE = prefix + "foundBleDevice"
    }

    object IntentKey {
        private const val prefix = "hojun.htbeyond.intent.key:"
        const val BT_DEVICE = prefix + "BleDevice"
    }

    object TestLatLng {
        // 위례성로
        // const val Lat = 37.5143
        // const val Lng = 127.1202
        // 학동역
        const val Lat = 37.511873
        const val Lng = 127.028846
    }
}