package com.example.htbeyond.viewmodel

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothAdapter: BluetoothAdapter? =
        application.getSystemService<BluetoothManager>()?.adapter
    private val uiEvent = MutableStateFlow(UiState())

    val uiStateFlow: StateFlow<UiState> =
        uiEvent.debounce(500).mapLatest { return@mapLatest it }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState()
        )

    init {
        checkStatus()
    }

    fun checkStatus(){
        uiEvent.value = uiStateFlow.value.copy(isBluetoothEnabled = isBluetoothHardwareAvailable(), isBluetoothGranted = isBluetoothPermissionGranted(), isBluetoothAvailable = isBluetoothEnabled())
    }

    private fun isBluetoothHardwareAvailable(): Boolean {
        return getApplication<Application>().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) && getApplication<Application>().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    private fun isBluetoothPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun getCurrentStatus() : UiState {
        return uiStateFlow.value
    }

    data class UiState(
        var isBluetoothAvailable: Boolean = false,
        var isBluetoothGranted: Boolean = false,
        var isBluetoothEnabled: Boolean = false
    )
}