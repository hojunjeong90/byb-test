package com.example.htbeyond.enum

import com.example.htbeyond.viewmodel.MainViewModel

enum class UiType {
    REQUEST_BLUETOOTH_ENABLE,
    REQUEST_BLUETOOTH_PERMISSION,
    ALL_PREPARED;

    companion object {
        fun from(uiState: MainViewModel.UiState): UiType {
            return when (uiState.isBluetoothGranted) {
                true -> {
                    when (uiState.isBluetoothEnabled) {
                        true -> {
                            ALL_PREPARED
                        }
                        false -> {
                            REQUEST_BLUETOOTH_ENABLE
                        }
                    }
                }
                false -> {
                    REQUEST_BLUETOOTH_PERMISSION
                }
            }
        }
    }
}