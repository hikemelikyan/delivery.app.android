package com.hmelikyan.deliveryapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmelikyan.deliveryapp.model.OrderDestinationModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val orderDestinationsLiveData: MutableLiveData<List<OrderDestinationModel>> by lazy { MutableLiveData<List<OrderDestinationModel>>() }

}