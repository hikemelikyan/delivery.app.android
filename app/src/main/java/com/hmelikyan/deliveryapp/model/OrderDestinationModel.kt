package com.hmelikyan.deliveryapp.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.hmelikyan.deliveryapp.DeliveryApp
import java.io.IOException
import java.util.*

class OrderDestinationModel(
    val orderId: Int,
    val address: String,
    val apartment: String,
    val lat: Double,
    val lng: Double,
    val deliveryStart: String,
    val deliveryEnd: String,
    val eta: String
) {
    var isActive: Boolean = false
    var isFinished: Boolean = false
        set(value) {
            field = value
            if (value) {
                isActive = false
            }
        }

    private fun getAddressFromLocation(context: Context): String? {
        val geocoder: Geocoder = Geocoder(context, Locale.US)
        val addresses: List<Address>
        var address: String? = null

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
                if (address!!.contains(addresses[0].countryName))
                    address = address.replace(", " + addresses[0].countryName, "")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return address
    }

}