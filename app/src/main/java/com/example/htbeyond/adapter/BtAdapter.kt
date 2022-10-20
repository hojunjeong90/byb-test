package com.example.htbeyond.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.htbeyond.model.BtUiModel
import com.example.htbeyond.model.BtDevice
import com.example.htbeyond.viewholder.BtDeviceViewHolder
import com.hjj.booksearcher.viewholders.HeaderViewHolder

class BtAdapter(private val onItemClick: BtDeviceViewHolder.OnItemClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet = ArrayList<BtUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1000 -> {
                HeaderViewHolder.create(parent)
            }
            else -> {
                BtDeviceViewHolder.create(parent, onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (dataSet[position]) {
            is BtUiModel.Header -> {
                (holder as HeaderViewHolder).bind(dataSet[position] as BtUiModel.Header)
            }
            is BtUiModel.Item -> {
                (holder as BtDeviceViewHolder).bind(dataSet[position] as BtUiModel.Item)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return when (dataSet[position]) {
            is BtUiModel.Header -> {
                1000
            }
            is BtUiModel.Item -> {
                1001
            }
        }
    }

    fun setData(data: BtDevice) {
        dataSet.find {
            it is BtUiModel.Item && it.btDevice.deviceAddress == data.deviceAddress
        }?.let {
//            val index = dataSet.indexOf(it)
//            dataSet.remove(it)
//            notifyItemRemoved(index)
        } ?: kotlin.run {
            dataSet.add(BtUiModel.Item(data, false))
            notifyItemInserted(dataSet.size)
        }
    }

    fun updateData(data: BtUiModel.Item){
        dataSet.find {
            it is BtUiModel.Item && it.btDevice.deviceAddress == data.btDevice.deviceAddress
        }?.let {
            val index = dataSet.indexOf(it)
            dataSet[index] = data
            notifyItemChanged(index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }
}