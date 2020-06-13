package com.hmelikyan.deliveryapp

import android.app.Application

class DeliveryApp : Application() {

    companion object {
        private lateinit var INSTANCE: DeliveryApp

        fun getInstance(): DeliveryApp {
            return INSTANCE
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

}