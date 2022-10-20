package com.example.htbeyond.model

sealed class BtUiModel {
    data class Header(val title: String, val count: Int): BtUiModel()
    data class Item(val btDevice: BtDevice, var isConnected: Boolean): BtUiModel()
}
