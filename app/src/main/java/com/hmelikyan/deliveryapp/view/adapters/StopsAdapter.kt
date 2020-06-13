package com.hmelikyan.deliveryapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.hmelikyan.deliveryapp.R
import com.hmelikyan.deliveryapp.databinding.AdapterStopItemBinding
import com.hmelikyan.deliveryapp.model.OrderDestinationModel
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class StopsAdapter(
    private val stopsList: List<OrderDestinationModel>,
    private val onNavigateClick: (Double, Double) -> Unit,
    private val onFinishClick: (OrderDestinationModel) -> Unit
) : RecyclerView.Adapter<StopsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterStopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = stopsList[position]

        holder.mBinding.orderId.text = stopsList[position].orderId.toString()
        holder.mBinding.orderNumberText.text = (position + 1).toString()
        holder.mBinding.apartment.text = order.apartment
        holder.mBinding.deliveringAddress.text = order.address
        holder.mBinding.deliveryTime.text = "${order.deliveryStart}-${order.deliveryEnd}"
        holder.mBinding.countDownTimerView.text = order.eta

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        var deliveryEnd: Date? = null
        var estimatedTimeOfArrival: Date? = null
        try {
            deliveryEnd = sdf.parse(order.deliveryEnd)
            estimatedTimeOfArrival = sdf.parse(order.eta)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (deliveryEnd != null && estimatedTimeOfArrival != null) {
            if (estimatedTimeOfArrival.before(deliveryEnd)) {
                holder.mBinding.countDownTimerView.setTextColor(holder.mBinding.root.context.resources.getColor(R.color.remaining_time_color))
                holder.mBinding.alertIcon.visibility = View.GONE
            } else {
                holder.mBinding.countDownTimerView.setTextColor(holder.mBinding.root.context.resources.getColor(R.color.expiring_time_color))
                holder.mBinding.alertIcon.visibility = View.VISIBLE
            }
        }

        when {
            order.isActive -> {
                holder.mBinding.deliveryTime.visibility = View.VISIBLE
                holder.mBinding.actionsLayout.visibility = View.VISIBLE
                holder.mBinding.countDownTimerView.visibility = View.VISIBLE
                holder.mBinding.finishedIcon.visibility = View.GONE
                holder.mBinding.orderNumberText.visibility = View.VISIBLE

                holder.mBinding.orderNumber.background = holder.mBinding.root.context.resources.getDrawable(R.drawable.active_order_number_background)
                holder.mBinding.cardLayout.background = holder.mBinding.root.context.resources.getColor(android.R.color.white).toDrawable()

                holder.mBinding.orderId.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.black))
                holder.mBinding.deliveringAddress.setTextColor(holder.mBinding.root.resources.getColor(R.color.tintGray))
                holder.mBinding.apartment.setTextColor(holder.mBinding.root.resources.getColor(R.color.tintGray))
                holder.mBinding.orderNumberText.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.white))

                val lp = holder.mBinding.rootLayout.layoutParams as RecyclerView.LayoutParams
                lp.setMargins(0, 0, 0, 0)
                holder.mBinding.rootLayout.layoutParams = lp
            }
            order.isFinished -> {
                holder.mBinding.orderNumber.background = holder.mBinding.root.context.resources.getDrawable(R.drawable.finished_order_number_background)
                holder.mBinding.cardLayout.background = holder.mBinding.root.context.resources.getColor(R.color.finished_card_background).toDrawable()

                holder.mBinding.orderId.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.white))
                holder.mBinding.deliveringAddress.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.white))
                holder.mBinding.apartment.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.white))
                holder.mBinding.orderNumberText.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.black))

                holder.mBinding.finishedIcon.visibility = View.VISIBLE
                holder.mBinding.orderNumberText.visibility = View.GONE
                holder.mBinding.countDownTimerView.visibility = View.GONE
                holder.mBinding.deliveryTime.visibility = View.GONE
                holder.mBinding.actionsLayout.visibility = View.GONE

                val lp = holder.mBinding.rootLayout.layoutParams as RecyclerView.LayoutParams
                lp.setMargins(0, 0, 0, 0)
                holder.mBinding.rootLayout.layoutParams = lp
            }
            else -> {
                holder.mBinding.orderNumber.background = holder.mBinding.root.context.resources.getDrawable(R.drawable.pending_order_number_background)
                holder.mBinding.cardLayout.background = holder.mBinding.root.context.resources.getColor(R.color.card_background).toDrawable()

                holder.mBinding.deliveryTime.visibility = View.VISIBLE
                holder.mBinding.countDownTimerView.visibility = View.VISIBLE
                holder.mBinding.actionsLayout.visibility = View.GONE
                holder.mBinding.finishedIcon.visibility = View.GONE
                holder.mBinding.orderNumberText.visibility = View.VISIBLE

                holder.mBinding.orderId.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.black))
                holder.mBinding.deliveringAddress.setTextColor(holder.mBinding.root.resources.getColor(R.color.tintGray))
                holder.mBinding.apartment.setTextColor(holder.mBinding.root.resources.getColor(R.color.tintGray))
                holder.mBinding.orderNumberText.setTextColor(holder.mBinding.root.resources.getColor(android.R.color.black))

                val lp = holder.mBinding.rootLayout.layoutParams as RecyclerView.LayoutParams
                lp.setMargins(10, 10, 10, 10)
                holder.mBinding.rootLayout.layoutParams = lp
            }
        }

        holder.mBinding.navigateLayout.setOnClickListener { onNavigateClick(order.lat, order.lng) }
        holder.mBinding.finishLayout.setOnClickListener {
            order.isFinished = true
            notifyItemChanged(position)
            if (position != stopsList.size - 1) {
                stopsList[position + 1].isActive = true
                notifyItemChanged(position + 1)
            }
            onFinishClick(order)
        }
    }


    inner class ViewHolder(val mBinding: AdapterStopItemBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun getItemCount(): Int {
        return stopsList.size
    }

}