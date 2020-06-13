package com.hmelikyan.deliveryapp.view.activity.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.hmelikyan.deliveryapp.databinding.FragmentStopsBinding
import com.hmelikyan.deliveryapp.model.OrderDestinationModel
import com.hmelikyan.deliveryapp.view.adapters.StopsAdapter
import com.hmelikyan.deliveryapp.view.root.BaseFragment
import com.hmelikyan.deliveryapp.viewModel.MainViewModel

class StopsFragment : BaseFragment() {
    private lateinit var mBinding: FragmentStopsBinding
    private val mViewModel: MainViewModel by activityViewModels()
    private var mAdapter: StopsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentStopsBinding.inflate(inflater, container, false)

        observingToLiveData()
        return mBinding.root
    }

    private fun observingToLiveData() {
        mViewModel.orderDestinationsLiveData.observe(viewLifecycleOwner, Observer {
            if (mAdapter == null) {
                mAdapter = StopsAdapter(it, ::onNavigateClick, ::onFinishClick)
                mBinding.stopsRecycler.adapter = mAdapter
            }
        })
    }

    private fun onFinishClick(orderModel: OrderDestinationModel) {
        val list = mViewModel.orderDestinationsLiveData.value
        if (list != null) {
            val indexOfOrder = list.indexOf(orderModel)
            list[indexOfOrder].isFinished = true
            if (indexOfOrder != list.size - 1) {
                list[indexOfOrder + 1].isActive = true
            }
        }
        mViewModel.orderDestinationsLiveData.postValue(list)
    }

    private fun onNavigateClick(lat: Double, lng: Double) {
        val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lng"))
        try {
            intentGoogle.setPackage("com.google.android.apps.maps")
            startActivity(intentGoogle)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "You don't have any navigation apps", Toast.LENGTH_LONG).show()
        }
    }
}