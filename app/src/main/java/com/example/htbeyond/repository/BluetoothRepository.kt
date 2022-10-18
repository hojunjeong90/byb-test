package com.example.htbeyond.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import com.example.htbeyond.model.BtDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest

class BluetoothRepository(private val bluetoothAdapter: BluetoothAdapter?) {


    val foundData: MutableStateFlow<BtDevice?> = MutableStateFlow(null)
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

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

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner?.stopScan(leScanCallback)
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        bluetoothLeScanner?.startScan(leScanCallback)
    }

    @SuppressLint("MissingPermission")
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val deviceName = result.device.name ?: result.scanRecord?.deviceName
            val deviceHardwareAddress = result.device.address
            val isConnectable = result.isConnectable
            val timestamp = result.timestampNanos
            val txPower = result.txPower
            val interval = result.periodicAdvertisingInterval
            Log.d("TAG", "BLUETOOTH FOUND : $deviceName, $deviceHardwareAddress")
            foundData.value = BtDevice(deviceName, deviceHardwareAddress, isConnectable, timestamp, txPower, interval)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.forEach { scanResult ->
                val deviceName = scanResult.device.name ?: scanResult.scanRecord?.deviceName
                val deviceHardwareAddress = scanResult.device.address
                Log.d("TAG", "BLUETOOTH FOUND : $deviceName, $deviceHardwareAddress")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("TAG", "$errorCode")
        }
    }
}