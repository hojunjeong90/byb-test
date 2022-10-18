package com.example.htbeyond.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.htbeyond.Constants
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.repository.BluetoothRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val uiEvent = MutableStateFlow(UiState())
    val bluetoothRepository: BluetoothRepository = BluetoothRepository(bluetoothAdapter)
    var isScanning = false

    val uiStateFlow: StateFlow<UiState> =
        uiEvent.mapLatest { return@mapLatest it }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState()
        )

    init {
        checkStatus()
    }

    fun checkStatus(){
        uiEvent.value = uiStateFlow.value.copy(isBluetoothEnabled = isBluetoothHardwareAvailable(), isBluetoothGranted = isBluetoothPermissionGranted(), isBluetoothAvailable = isBluetoothEnabled(), isBluetoothScanning = isScanning)
    }

    private fun isBluetoothHardwareAvailable(): Boolean {
        return getApplication<Application>().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) && getApplication<Application>().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    private fun isBluetoothPermissionGranted(): Boolean {
        Constants.PERMISSIONS.forEach { permission ->
            if((ActivityCompat.checkSelfPermission(getApplication(), permission) != PackageManager.PERMISSION_GRANTED)){
                return false
            }
        }
        return true
    }

    private fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun getCurrentStatus() : UiState {
        return uiStateFlow.value
    }

    fun startBluetoothScanning() {
        bluetoothRepository.startScan()
        isScanning = true
        checkStatus()
    }

    fun stopBluetoothScanning() {
        bluetoothRepository.stopScan()
        isScanning = false
        checkStatus()
    }

    data class UiState(
        var isBluetoothAvailable: Boolean = false,
        var isBluetoothGranted: Boolean = false,
        var isBluetoothEnabled: Boolean = false,
        var isBluetoothScanning: Boolean = false
    )

}