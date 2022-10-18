package com.example.htbeyond.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.htbeyond.model.BluetoothUiModel
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.viewholder.BtDeviceViewHolder
import com.hjj.booksearcher.viewholders.HeaderViewHolder

class BtAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet = ArrayList<BluetoothUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1000 ->{
                HeaderViewHolder.create(parent)
            }
            else ->{
                BtDeviceViewHolder.create(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(dataSet[position]){
            is BluetoothUiModel.Header -> {
                (holder as HeaderViewHolder).bind(dataSet[position] as BluetoothUiModel.Header)
            }
            is BluetoothUiModel.Item -> {
                (holder as BtDeviceViewHolder).bind(dataSet[position] as BluetoothUiModel.Item)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return when(dataSet[position]){
            is BluetoothUiModel.Header -> {
                1000
            }
            is BluetoothUiModel.Item -> {
                1001
            }
        }
    }

    fun setData(data: BtDevice){
        dataSet.find {
            it is BluetoothUiModel.Item && it.btDevice.deviceAddress == data.deviceAddress
        }?.let {
//            val index = dataSet.indexOf(it)
//            dataSet.remove(it)
//            notifyItemRemoved(index)
        } ?: kotlin.run {
            dataSet.add(BluetoothUiModel.Item(data))
            notifyItemInserted(dataSet.size)
        }

    }

    fun clearData(){
        dataSet.clear()
        notifyDataSetChanged()
    }
}