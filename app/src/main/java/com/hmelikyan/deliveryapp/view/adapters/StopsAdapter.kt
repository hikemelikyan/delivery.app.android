package com.hmelikyan.deliveryapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmelikyan.deliveryapp.databinding.AdapterStopItemBinding
import com.hmelikyan.deliveryapp.model.OrderDestinationModel

class StopsAdapter(private val stepsList: ArrayList<OrderDestinationModel>) : RecyclerView.Adapter<StopsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterStopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return stepsList.size
    }

    inner class ViewHolder(val mBinding: AdapterStopItemBinding) : RecyclerView.ViewHolder(mBinding.root)

}