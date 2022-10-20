package com.example.htbeyond.service

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.example.htbeyond.Constants
import com.example.htbeyond.R
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.view.MainActivity

@SuppressLint("MissingPermission")
class BtGattService : Service() {

    private val TAG = "BtGattService"

    private val mBinder: IBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val getService: BtGattService
            get() = this@BtGattService
    }

    private var bleGatts: ArrayList<BluetoothGatt> = ArrayList()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Action Received = ${intent?.action}")
        when (intent?.action) {
            Constants.IntentAction.START_GATT_FOREGROUND -> {
                val btDevice = intent.getParcelableExtra<BtDevice>(Constants.IntentKey.BT_DEVICE)
                startForegroundService(btDevice)
            }
            Constants.IntentAction.DISCONNECT_DEVICE -> {
                val btDevice = intent.getParcelableExtra<BtDevice>(Constants.IntentKey.BT_DEVICE)
                disconnectGattServer(btDevice?.bluetoothDevice)
            }
            Constants.IntentAction.STOP_GATT_FOREGROUND -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    private fun startForegroundService(btDevice: BtDevice?) {
        val channelId = createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Service is running in background")
            .setContentText("Tap to open")
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(2, notification)

        connectDevice(btDevice?.bluetoothDevice)
    }

    private fun connectDevice(device: BluetoothDevice?) {
        broadcastMessage("Connecting to ${device?.address}")
        device?.connectGatt(applicationContext, false, gattClientCallback)?.let {
            bleGatts.add(it)
        }
    }

    private val gattClientCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                broadcastConnected(gatt)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastDisconnected(gatt)
            }
        }
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel(): String {
        val channelId = "BluetoothGatt"
        val channelName = "블루투스 연결"
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.BLUE
                importance = NotificationManager.IMPORTANCE_NONE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            notificationChannel
        )
        return channelId
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        disconnectGattServer()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
    }

    private fun broadcastMessage(msg: String) {
        val intent = Intent(Constants.IntentAction.STATUS_MSG).putExtra(Constants.IntentKey.MSG_DATA, msg)
        sendBroadcast(intent)
    }

    private fun broadcastConnected(bleGatt: BluetoothGatt) {
        val intent = Intent(Constants.IntentAction.GATT_CONNECTED).putExtra(
            Constants.IntentKey.BLUETOOTH_DEVICE,
            bleGatt.device
        )
        sendBroadcast(intent)
    }

    private fun broadcastDisconnected(bleGatt: BluetoothGatt) {
        val intent = Intent(Constants.IntentAction.GATT_DISCONNECTED).putExtra(
            Constants.IntentKey.BLUETOOTH_DEVICE,
            bleGatt.device
        )
        sendBroadcast(intent)
    }

    private fun disconnectGattServer(bluetoothDevice: BluetoothDevice? = null) {
        bluetoothDevice?.let { device ->
            val foundDevice = bleGatts.find { it.device.address == device.address }
            foundDevice?.let {
                broadcastDisconnected(it)
                it.close()
                bleGatts.remove(it)
            }
        } ?: run {
            bleGatts.forEach { gatt ->
                broadcastDisconnected(gatt)
                gatt.close()
            }
            bleGatts.clear()
        }
    }
}