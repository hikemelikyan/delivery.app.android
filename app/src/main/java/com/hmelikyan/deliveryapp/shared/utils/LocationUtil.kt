package com.hmelikyan.deliveryapp.shared.utils

import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

object LocationUtil {

    fun createLocationRequest(receiverActivity: AppCompatActivity, requestCode: Int) {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 10000 shr 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(receiverActivity)
        val task = settingsClient.checkLocationSettings(locationSettingsRequest.build())

        task.addOnSuccessListener {
            //ok
        }

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(receiverActivity, requestCode)
                } catch (e: IntentSender.SendIntentException) {
                    //handle
                }
            }
        }
    }

    fun isGPSEnabled(context: Context, locationManager: LocationManager): Boolean {
        return if (isAirplaneModeOn(context)) {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.ACTION_AIRPLANE_MODE_SETTINGS, 0) != 0
    }

}
