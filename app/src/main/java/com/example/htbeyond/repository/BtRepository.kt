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
import com.example.htbeyond.service.BtScanService
import com.example.htbeyond.util.Event
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * TODO : SCAN 결과를 RECEIVER가 아니라 ROOM에 저장하고 정리된 데이터로 읽어올 수 있다.
 */
class BtRepository {

    private val TAG = this::class.java.simpleName
    val foundData: MutableStateFlow<BtDevice?> = MutableStateFlow(null)

    fun registerScanReceiver(){
        MyApplication.applicationContext().registerReceiver(scanReceiver, getScanIntentFilter())
    }

    fun unregisterScanReceiver(){
        MyApplication.applicationContext().unregisterReceiver(scanReceiver)
    }

    fun startScanService(){
        Intent(MyApplication.applicationContext(), BtScanService::class.java).apply {
            action = Constants.IntentAction.START_FOREGROUND
        }.run {
            MyApplication.applicationContext().startForegroundService(this)
        }
    }
    fun stopScanService(){
        Intent(MyApplication.applicationContext(), BtScanService::class.java).apply {
            action = Constants.IntentAction.STOP_FOREGROUND
        }.run {
            MyApplication.applicationContext().startForegroundService(this)
            MyApplication.applicationContext().stopService(this)
        }
    }

    private fun getScanIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Constants.IntentAction.FOUND_BT_DEVICE)
        }
    }

    private var scanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                Constants.IntentAction.FOUND_BT_DEVICE->{
                    val btDevice = intent.getParcelableExtra<BtDevice>(Constants.IntentKey.BT_DEVICE)
                    btDevice?.let {
                        foundData.value = it
                    }
                }
            }
        }
    }

    var deviceToConnect: BluetoothDevice? = null
    var cmdByteArray: ByteArray? = null
    var isConnected = MutableLiveData<Event<Boolean>>()
    var statusTxt: String = ""
    var isRead = false
    var isStatusChange: Boolean = false
    val readDataFlow = MutableStateFlow("")

    /**
     * Disconnect Gatt Server
     */
    fun disconnectGattServer() {
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            //MyApplication.applicationContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
            intent.action = Constants.IntentAction.DISCONNECT_DEVICE
            MyApplication.applicationContext().startForegroundService(intent)
        }
        deviceToConnect = null
    }

    fun writeData(byteArray: ByteArray){
        cmdByteArray = byteArray
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            //MyApplication.applicationContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
            intent.action = Constants.IntentAction.WRITE_DATA
            MyApplication.applicationContext().startForegroundService(intent)
        }
    }
    fun readToggle(){
        if(isRead){
            isRead = false
            stopNotification()
        }else{
            isRead = true
            startNotification()
        }
    }
    private fun startNotification(){
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            //MyApplication.applicationContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
            intent.action = Constants.IntentAction.START_NOTIFICATION
            MyApplication.applicationContext().startForegroundService(intent)
        }
    }
    private fun stopNotification(){
        Intent(MyApplication.applicationContext(), BtGattService::class.java).also { intent ->
            //MyApplication.applicationContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
            intent.action = Constants.IntentAction.STOP_NOTIFICATION
            MyApplication.applicationContext().startForegroundService(intent)
        }
    }


    private var bleGattReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG,"action ${intent.action}")
            when(intent.action){
                Constants.IntentAction.FOUND_BT_DEVICE->{
                    val btDevice = intent.getParcelableExtra<BtDevice>(Constants.IntentKey.BT_DEVICE)
                    btDevice?.let {
                        foundData.value = it
                    }
                }
                Constants.IntentAction.GATT_CONNECTED-> {
                    isConnected.postValue(Event(true))
                    intent.getStringExtra(Constants.IntentAction.MSG_DATA)?.let {
                        statusTxt = it
                        isStatusChange = true
                    }
                }
                Constants.IntentAction.GATT_DISCONNECTED->{
                    stopScanService()
                    isConnected.postValue(Event(false))
                    intent.getStringExtra(Constants.IntentAction.MSG_DATA)?.let{
                        statusTxt = it
                        isStatusChange = true
                    }
                }
                Constants.IntentAction.STATUS_MSG->{
                    intent.getStringExtra(Constants.IntentAction.MSG_DATA)?.let{
                        statusTxt = it
                        isStatusChange = true
                    }
                }
                Constants.IntentAction.READ_CHARACTERISTIC->{
                    intent.getByteArrayExtra(Constants.IntentAction.READ_BYTES)?.let{ bytes->
                        val hexString: String = bytes.joinToString(separator = " ") {
                            String.format("%02X", it)
                        }
                        readDataFlow.value = hexString
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