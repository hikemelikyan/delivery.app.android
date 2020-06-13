package com.hmelikyan.deliveryapp.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class OrderDestinationModel(
    val address: String,
    val apartment: String,
    val lat: Double,
    val lng: Double
) : BaseObservable() {

    var isActive: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyChange()
        }
    var isFinished: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyChange()
        }

}