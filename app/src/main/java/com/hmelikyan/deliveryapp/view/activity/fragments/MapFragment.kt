package com.hmelikyan.deliveryapp.view.activity.fragments

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.hmelikyan.deliveryapp.R
import com.hmelikyan.deliveryapp.databinding.FragmentMapBinding
import com.hmelikyan.deliveryapp.shared.utils.ImageUtil
import com.hmelikyan.deliveryapp.view.activity.MainActivity
import com.hmelikyan.deliveryapp.view.root.BaseFragment
import com.hmelikyan.deliveryapp.viewModel.MainViewModel

class MapFragment : BaseFragment(), OnMapReadyCallback {
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
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        activityViewModel.orderDestinationsLiveData.observe(viewLifecycleOwner, Observer {
            val builder = LatLngBounds.builder()
            for (destination in it) {
                builder.include(LatLng(destination.lat, destination.lng))
            }
            latLngBounds = builder.build()
            mMap?.clear()
            for (address in it) {
                val pinDrawable = BitmapDescriptorFactory.fromBitmap(
                    ImageUtil.createBitmapFromDrawable(
                        when {
                            address.isActive -> resources.getDrawable(R.drawable.map_pin_icon_delivering)
                            address.isFinished -> resources.getDrawable(R.drawable.map_pin_icon_finished)
                            else -> resources.getDrawable(R.drawable.map_pin_icon_pending)
                        }
                    )
                )
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
            mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, getDisplayWidth() / 5))
        } else {
            initMap()
        }
    }

    private fun getDisplayWidth(): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}