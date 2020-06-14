package com.hmelikyan.deliveryapp.view.activity.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.hmelikyan.deliveryapp.DeliveryApp
import com.hmelikyan.deliveryapp.R
import com.hmelikyan.deliveryapp.databinding.FragmentMapBinding
import com.hmelikyan.deliveryapp.shared.utils.ImageUtil
import com.hmelikyan.deliveryapp.shared.utils.LocationUtil
import com.hmelikyan.deliveryapp.view.activity.MainActivity
import com.hmelikyan.deliveryapp.view.root.BaseFragment
import com.hmelikyan.deliveryapp.viewModel.MainViewModel

private const val LOCATION_SETTINGS_REQUEST_CODE = 78
private const val LOCATION_SETTINGS_OS_REQUEST_CODE = 865
private const val LOCATION_PERMISSIONS_REQUEST_CODE = 542

class MapFragment : BaseFragment(), OnMapReadyCallback, LocationListener {
    private lateinit var mBinding: FragmentMapBinding
    private lateinit var mLocMan: LocationManager
    private lateinit var mActivity: MainActivity
    private val activityViewModel: MainViewModel by activityViewModels()
    private var mMap: GoogleMap? = null
    private var latLngBounds: LatLngBounds? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mActivity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentMapBinding.inflate(inflater, container, false)
        mLocMan = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        initMap()
        return mBinding.root
    }

    private fun initMap() {
        val supportMap = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMap.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        checkLocationSettings()
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        activityViewModel.orderDestinationsLiveData.observe(viewLifecycleOwner, Observer {
            val builder = LatLngBounds.builder()
            for (destination in it) {
                builder.include(LatLng(destination.lat, destination.lng))
            }
            latLngBounds = builder.build()
            mMap?.clear()
            for (address in it) {
                val pinDrawable = when {
                    address.isActive -> BitmapDescriptorFactory.fromBitmap(ImageUtil.createBitmapFromDrawable(resources.getDrawable(R.drawable.map_pin_icon_delivering)))
                    address.isFinished -> BitmapDescriptorFactory.fromBitmap(ImageUtil.createBitmapFromDrawable(resources.getDrawable(R.drawable.map_pin_icon_finished)))
                    else -> BitmapDescriptorFactory.fromBitmap(ImageUtil.createBitmapFromDrawable(resources.getDrawable(R.drawable.map_pin_icon_pending)))
                }
                mMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(address.lat, address.lng))
                        .anchor(1f, 1f)
                        .icon(pinDrawable)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (mMap != null) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, getDisplayWidth() / 3))
        } else {
            initMap()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationSettings() {
        if (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (!LocationUtil.isGPSEnabled(requireContext(), mLocMan)) {
                LocationUtil.createLocationRequest(mActivity, LOCATION_SETTINGS_REQUEST_CODE)
            } else {
                mMap?.isMyLocationEnabled = true
                getLastKnownLocation()
            }
        } else {
            requestPermissionsSafely(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSIONS_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        LocationServices.getFusedLocationProviderClient(mActivity)
            .lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 13f))
                } else {
                    mLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, this)
                    mLocMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10f, this)
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings()
            } else {
                if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                    !shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    startActivityForResult(createIntentToApplicationPackage(), LOCATION_SETTINGS_OS_REQUEST_CODE)
                } else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCATION_SETTINGS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    checkLocationSettings()
                }
            }
            LOCATION_SETTINGS_OS_REQUEST_CODE -> {
                if (hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) || hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION))
                    checkLocationSettings()
            }
        }
    }

    private fun createIntentToApplicationPackage(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(
            "package",
            DeliveryApp.getInstance().packageName,
            null
        )
        intent.data = uri
        return intent
    }

    override fun onLocationChanged(location: Location?) {
        checkLocationSettings()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        checkLocationSettings()
    }

    override fun onProviderEnabled(provider: String?) {
        checkLocationSettings()
    }

    override fun onProviderDisabled(provider: String?) {
        checkLocationSettings()
    }

    fun getDisplayWidth(): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}