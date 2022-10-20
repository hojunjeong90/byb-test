package com.example.htbeyond.repository

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.htbeyond.Constants
import com.example.htbeyond.MyApplication
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.model.BtUiModel
import com.example.htbeyond.service.BtScanService
import com.example.htbeyond.service.BtGattService
import com.example.htbeyond.util.Event
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * TODO : SCAN 결과를 RECEIVER가 아니라 ROOM에 저장하고 정리된 데이터로 읽어올 수 있다.
 */
object BtRepository {

    private val TAG = this::class.java.simpleName
    val foundDevice: MutableStateFlow<BtDevice?> = MutableStateFlow(null)
    val btUiModelStateFlow: MutableStateFlow<BtUiModel.Item?> = MutableStateFlow(null)
    var statusText = MutableStateFlow("")

    fun registerScanReceiver() {
        MyApplication.applicationContext().registerReceiver(scanReceiver, getScanIntentFilter())
    }

    fun unregisterScanReceiver() {
        MyApplication.applicationContext().unregisterReceiver(scanReceiver)
    }

    fun startScanService() {
        Intent(MyApplication.applicationContext(), BtScanService::class.java).apply {
            action = Constants.IntentAction.START_SCAN_FOREGROUND
        }.run {
            MyApplication.applicationContext().startForegroundService(this)
        }
    }

    fun stopScanService() {
        Intent(MyApplication.applicationContext(), BtScanService::class.java).apply {
            action = Constants.IntentAction.STOP_SCAN_FOREGROUND
        }.run {
            MyApplication.applicationContext().startForegroundService(this)
            MyApplication.applicationContext().stopService(this)
        }
    }

    private fun getScanIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Constants.IntentAction.SCAN_BT_DEVICE)
        }
    }

    private fun getReceiverIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Constants.IntentAction.GATT_CONNECTED)
            addAction(Constants.IntentAction.GATT_DISCONNECTED)
            addAction(Constants.IntentAction.STATUS_MSG)
            addAction(Constants.IntentKey.MSG_DATA)
        }
    }

    private var scanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.IntentAction.SCAN_BT_DEVICE -> {
                    val btDevice =
                        intent.getParcelableExtra<BtDevice>(Constants.IntentKey.BT_DEVICE)
                    btDevice?.let {
                        foundDevice.value = it
                    }
                }
            }
        }
    }

    fun registerGattReceiver() {
        MyApplication.applicationContext()
            .registerReceiver(bleGattReceiver, getReceiverIntentFilter())
    }

    fun unregisterGattReceiver() {
        MyApplication.applicationContext().unregisterReceiver(bleGattReceiver)
    }

    fun disconnectGattServer(btDevice: BtDevice) {
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            intent.action = Constants.IntentAction.DISCONNECT_DEVICE
            intent.putExtra(Constants.IntentKey.BT_DEVICE, btDevice)
            MyApplication.applicationContext().startForegroundService(intent)
        }
    }

    fun connectGattServer(btDevice: BtDevice) {
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            intent.action = Constants.IntentAction.START_GATT_FOREGROUND
            intent.putExtra(Constants.IntentKey.BT_DEVICE, btDevice)
            MyApplication.applicationContext().startForegroundService(intent)
        }
    }

    private fun stopForegroundService() {
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            intent.action = Constants.IntentAction.STOP_GATT_FOREGROUND
            MyApplication.applicationContext().startForegroundService(intent)
        }
    }

    private var bleGattReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "action ${intent.action}")
            when (intent.action) {
                Constants.IntentAction.STATUS_MSG -> {
                    intent.getStringExtra(Constants.IntentKey.MSG_DATA)?.let {
                        statusText.value = it
                        Log.d(TAG, statusText.value)
                    }
                }
                Constants.IntentAction.GATT_CONNECTED -> {
                    val bluetoothDevice =
                        intent.getParcelableExtra<BluetoothDevice>(Constants.IntentKey.BLUETOOTH_DEVICE)
                    bluetoothDevice?.let {
                        btUiModelStateFlow.value = BtUiModel.Item(
                            BtDevice.setBluetoothDeviceToBtDevice(bluetoothDevice),
                            true
                        )
                    }
                }
                Constants.IntentAction.GATT_DISCONNECTED -> {
                    val bluetoothDevice =
                        intent.getParcelableExtra<BluetoothDevice>(Constants.IntentKey.BLUETOOTH_DEVICE)
                    bluetoothDevice?.let {
                        btUiModelStateFlow.value = BtUiModel.Item(
                            BtDevice.setBluetoothDeviceToBtDevice(bluetoothDevice),
                            false
                        )
                    }
                }
            }

        }
    }
//    @SuppressLint("MissingPermission")
//    fun getPairedDevices() {
//        val btDevices = ArrayList<BtDevice>()
//        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//        pairedDevices?.forEach { device ->
//            val deviceName = device.name
//            val deviceHardwareAddress = device.address // MAC address
//            btDevices.add(BtDevice(deviceName, deviceHardwareAddress))
//        }
//    }

}