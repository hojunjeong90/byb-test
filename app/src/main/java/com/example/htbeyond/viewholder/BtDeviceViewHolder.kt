package com.example.htbeyond.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.htbeyond.R
import com.example.htbeyond.databinding.ItemBtdeviceBinding
import com.example.htbeyond.model.BtUiModel
import com.example.htbeyond.model.BtDevice

class BtDeviceViewHolder(private val binding: ItemBtdeviceBinding, private val delegate: OnItemClick?=null) : RecyclerView.ViewHolder(binding.root) {

    interface OnItemClick {
        fun onConnectClick(position: Int, btDevice: BtDevice)
        fun onDisconnectClick(position: Int, btDevice: BtDevice)
    }

    private lateinit var btDevice: BtDevice

    init {
        itemView.setOnClickListener {
            if(this::btDevice.isInitialized && btDevice.isConnectable){
                delegate?.onConnectClick(adapterPosition, btDevice)
            }
        }
    }

    fun bind(item: BtUiModel.Item) {
        btDevice = item.btDevice
        with(binding) {
            deviceNameTextView.text =
            if (btDevice.deviceName == null){
                btDevice.deviceAddress
            } else {
                btDevice.deviceName + "(" + btDevice.deviceAddress + ")"
            }
            deviceAddressTextView.text = "Connectable : ${btDevice.isConnectable} / TxPwr : ${btDevice.txPower} / Interval : ${btDevice.interval}"
        }
    }

    companion object {
        fun create(parent: ViewGroup, delegate: OnItemClick?=null): BtDeviceViewHolder {
            LayoutInflater.from(parent.context).inflate(R.layout.item_btdevice, parent, false).run {
                ItemBtdeviceBinding.bind(this)
            }.run {
                return BtDeviceViewHolder(this, delegate)
            }
        }
    }
}