package com.example.htbeyond.service

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.htbeyond.Constants
import com.example.htbeyond.R
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.view.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY

@SuppressLint("MissingPermission")
class BtScanService : Service() {

    private val TAG = "BtScanService"

    /**
     * 특정 위치에서 {SCAN_AVAILABLE_LOCATION_DISTANCE}미터 이내에 있을 시
     * {SCAN_LOCATION_INTERVAL}/1000초 간격으로 위치를 읽어오고
     * {SCAN_BLE_PERIOD}/1000초 동안 블루투스 스캐닝 진행
     */
    private val SCAN_AVAILABLE_LOCATION_DISTANCE: Float = 1000f
    private val SCAN_LOCATION_INTERVAL: Long = 10 * 1000
    private val SCAN_BLE_PERIOD: Long = 3 * 1000
    private val scanHandler = Handler()
    private var scanning = false

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if(bluetoothLeScanner == null){
                    stopListening()
                }
                for (location in locationResult.locations) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        Constants.TestLatLng.Lat,
                        Constants.TestLatLng.Lng,
                        location.latitude,
                        location.longitude,
                        results
                    )
                    Log.v(TAG, "Distance : " + results[0].toString())
                    if (results[0] <= SCAN_AVAILABLE_LOCATION_DISTANCE) {
                        startBleScan()
                    } else {
                        stopBleScan()
                    }
                }
            }
        }
    }

    private val locationRequest = LocationRequest.Builder(SCAN_LOCATION_INTERVAL)
        .setPriority(PRIORITY_BALANCED_POWER_ACCURACY).build()

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy { bluetoothAdapter?.bluetoothLeScanner }

    override fun onBind(intent: Intent): IBinder {
        return this.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Action Received = ${intent?.action}")
        when (intent?.action) {
            Constants.IntentAction.START_FOREGROUND -> {
                startForegroundService()
            }
            Constants.IntentAction.STOP_FOREGROUND -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }


    private fun startForegroundService() {
        val channelId = createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("블루투스 탐색 중입니다.")
            .setContentText("탭하여 앱 열기")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        startListening()
    }

    private fun stopForegroundService() {
        stopListening()

        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel(): String {
        val channelId = "BackgroundBluetoothScanning"
        val channelName = "백그라운드 블루투스 탐색"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
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
            Log.d(TAG, "onScanResult : $deviceHardwareAddress")
            sendBtDevice(
                btDevice = BtDevice(
                    deviceName,
                    deviceHardwareAddress,
                    isConnectable,
                    timestamp,
                    txPower,
                    interval
                )
            )
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> Log.d(TAG, "SCAN_FAILED_ALREADY_STARTED")
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> Log.d(
                    TAG,
                    "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED"
                )
                SCAN_FAILED_FEATURE_UNSUPPORTED -> Log.d(TAG, "SCAN_FAILED_FEATURE_UNSUPPORTED")
                SCAN_FAILED_INTERNAL_ERROR -> Log.d(TAG, "SCAN_FAILED_INTERNAL_ERROR")
                SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> Log.d(
                    TAG,
                    "SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES"
                )
                SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> Log.d(
                    TAG,
                    "SCAN_FAILED_SCANNING_TOO_FREQUENTLY"
                )
            }
        }
    }

    /**
     * TODO 여러 상태를 브로드캐스트
     */
    private fun sendBtDevice(btDevice: BtDevice) {
        val intent = Intent(Constants.IntentAction.FOUND_BT_DEVICE)
        intent.putExtra(Constants.IntentKey.BT_DEVICE, btDevice)
        sendBroadcast(intent)
    }

    private fun startListening() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopListening() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startBleScan(){
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            scanHandler.postDelayed({
                scanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
                Log.d(TAG, "Stop After $SCAN_BLE_PERIOD")
            }, SCAN_BLE_PERIOD)
            scanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
            Log.d(TAG, "Scan For $SCAN_BLE_PERIOD")
        }
    }

    private fun stopBleScan(){
        bluetoothLeScanner?.stopScan(leScanCallback)
    }
}