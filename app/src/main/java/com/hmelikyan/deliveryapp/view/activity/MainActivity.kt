package com.hmelikyan.deliveryapp.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.hmelikyan.deliveryapp.R
import com.hmelikyan.deliveryapp.databinding.ActivityMainBinding
import com.hmelikyan.deliveryapp.model.FragmentPagerModel
import com.hmelikyan.deliveryapp.model.OrderDestinationModel
import com.hmelikyan.deliveryapp.shared.delegates.contentView
import com.hmelikyan.deliveryapp.shared.delegates.viewModelProvider
import com.hmelikyan.deliveryapp.view.activity.fragments.MapFragment
import com.hmelikyan.deliveryapp.view.activity.fragments.StopsFragment
import com.hmelikyan.deliveryapp.view.adapters.FragmentViewPagerAdapter
import com.hmelikyan.deliveryapp.view.root.BaseActivity
import com.hmelikyan.deliveryapp.viewModel.MainViewModel
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class MainActivity : BaseActivity() {
    private val mBinding: ActivityMainBinding by contentView(R.layout.activity_main)
    private val mViewModel: MainViewModel by viewModelProvider()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val destinations = getDataFromJson()
        destinations[0].isActive = true
        mViewModel.orderDestinationsLiveData.postValue(destinations)

        val fragmentsList = arrayListOf(
            FragmentPagerModel(getString(R.string.stops_title), StopsFragment()),
            FragmentPagerModel(getString(R.string.map_title), MapFragment())
        )

        val pagerAdapter = FragmentViewPagerAdapter(supportFragmentManager, fragmentsList)
        mBinding.fragmentPager.adapter = pagerAdapter
        mBinding.fragmentTabs.setupWithViewPager(mBinding.fragmentPager)

        mViewModel.orderDestinationsLiveData.observe(this, Observer {
            val tab = mBinding.fragmentTabs.getTabAt(0)
            val finishedOrders = it.filter { order ->
                order.isFinished
            }
            if (it.size != finishedOrders.size)
                tab?.text = "${getString(R.string.stops_title)}(${it.size - finishedOrders.size})"
            else
                tab?.text = getString(R.string.stops_title)
        })
    }

    private fun getDataFromJson(): List<OrderDestinationModel> {
        var json: String? = null
        try {
            val inputStream: InputStream = assets.open("locations.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val jElement = JsonParser().parse(json)
        val gson = Gson()
        return gson.fromJson(jElement, object : TypeToken<List<OrderDestinationModel>>() {}.type)
    }
}