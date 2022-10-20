package com.hjj.booksearcher.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.htbeyond.R
import com.example.htbeyond.databinding.ItemHeaderBinding
import com.example.htbeyond.model.BtUiModel

class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(headerItem: BtUiModel.Header) {
        binding.headerTextView.text = headerItem.title
    }

    companion object {
        fun create(parent: ViewGroup): HeaderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
            val binding = ItemHeaderBinding.bind(view)
            return HeaderViewHolder(binding)
        }
    }
}