package com.example.htbeyond.viewmodel

import android.app.ActivityManager
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.htbeyond.Constants
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.model.BtUiModel
import com.example.htbeyond.repository.BtRepository
import com.example.htbeyond.service.BtScanService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        (application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val activityManager =
        application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val uiEvent = MutableStateFlow(UiState())
    private val btRepository = BtRepository

    val uiStateFlow: StateFlow<UiState> =
        uiEvent.mapLatest { return@mapLatest it }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState()
        )

    init {
        checkStatus()
    }

    fun getFoundDevice(): MutableStateFlow<BtDevice?> {
        return btRepository.foundDevice
    }

    fun getBtUiFlow(): MutableStateFlow<BtUiModel.Item?> {
        return btRepository.btUiModelStateFlow
    }

    fun getStatusMessage(): MutableStateFlow<String> {
        return btRepository.statusText
    }

    fun checkStatus() {
        uiEvent.value = uiStateFlow.value.copy(
            isBluetoothEnabled = isBluetoothHardwareAvailable(),
            isBluetoothGranted = isBluetoothPermissionGranted(),
            isBluetoothAvailable = isBluetoothEnabled(),
            isBluetoothScanning = isBleScanningRunning()
        )
    }

    private fun isBleScanningRunning(): Boolean {
        activityManager.getRunningServices(Int.MAX_VALUE)
            .find { it.service.className.equals(BtScanService::class.java.name) }?.let {
            return true
        } ?: run {
            return false
        }
    }

    private fun isBluetoothHardwareAvailable(): Boolean {
        return getApplication<Application>().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) && getApplication<Application>().packageManager.hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE
        )
    }

    private fun isBluetoothPermissionGranted(): Boolean {
        Constants.PERMISSIONS.forEach { permission ->
            if ((ActivityCompat.checkSelfPermission(
                    getApplication(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                return false
            }
        }
        return true
    }

    private fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun getCurrentStatus(): UiState {
        return uiStateFlow.value
    }

    fun startBluetoothScanning() {
        btRepository.startScanService()
        checkStatus()
    }

    fun stopBluetoothScanning() {
        btRepository.stopScanService()
        checkStatus()
    }

    fun registerScanReceiver() {
        btRepository.registerScanReceiver()
    }

    fun unregisterScanReceiver() {
        btRepository.unregisterScanReceiver()
    }

    fun registerGattReceiver() {
        btRepository.registerGattReceiver()
    }

    fun unregisterGattReceiver() {
        btRepository.unregisterGattReceiver()
    }

    fun connectToDevice(btDevice: BtDevice) {
        btRepository.connectGattServer(btDevice)
    }

    fun disconnectToDevice(btDevice: BtDevice) {
        btRepository.disconnectGattServer(btDevice)
    }

    override fun onCleared() {
        super.onCleared()
        unregisterScanReceiver()
        unregisterGattReceiver()
    }

    data class UiState(
        var isBluetoothAvailable: Boolean = false,
        var isBluetoothGranted: Boolean = false,
        var isBluetoothEnabled: Boolean = false,
        var isBluetoothScanning: Boolean = false
    )
}