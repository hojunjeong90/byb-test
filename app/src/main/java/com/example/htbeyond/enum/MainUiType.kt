package com.example.htbeyond.enum

import com.example.htbeyond.viewmodel.MainViewModel

enum class MainUiType {

    NEED_BLUETOOTH_ENABLE,
    NEED_PERMISSION,
    PREPARED,
    SCANNING;

    companion object {
        fun from(uiState: MainViewModel.UiState): MainUiType {
            return when (uiState.isBluetoothGranted) {
                true -> {
                    when (uiState.isBluetoothEnabled) {
                        true -> {
                            when(uiState.isBluetoothScanning){
                                true ->{
                                    SCANNING
                                }
                                false->{
                                    PREPARED
                                }
                            }
                        }
                        false -> {
                            NEED_BLUETOOTH_ENABLE
                        }
                    }
                }
                false -> {
                    NEED_PERMISSION
                }
            }
        }
    }
}