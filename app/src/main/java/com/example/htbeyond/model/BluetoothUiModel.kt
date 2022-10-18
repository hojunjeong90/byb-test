package com.example.htbeyond.model

sealed class BluetoothUiModel {
    data class Header(val title: String, val count: Int): BluetoothUiModel()
    data class Item(val btDevice: BtDevice): BluetoothUiModel()
}
