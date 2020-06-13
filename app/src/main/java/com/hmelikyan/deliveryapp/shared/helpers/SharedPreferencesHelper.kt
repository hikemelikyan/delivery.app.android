package com.hmelikyan.deliveryapp.shared.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesHelper(context: Context) {
    private val mShared: SharedPreferences = context.getSharedPreferences("Configs", Context.MODE_PRIVATE)

    fun setStringSharedPreferences(key: String, value: String) = mShared.edit(true) { putString(key, value) }

    fun getStringSharedPreferences(key: String): String? = mShared.getString(key, null)

    fun getStringSharedPreferences(key: String, defValue: String): String? = mShared.getString(key, defValue)

    fun setIntSharedPreferences(key: String, value: Int) = mShared.edit(true) { putInt(key, value) }

    fun getIntSharedPreferences(key: String): Int = mShared.getInt(key, 0)

    fun setBooleanSharedPreferences(key: String, value: Boolean) = mShared.edit(true) { putBoolean(key, value) }

    fun getBooleanSharedPreferences(key: String): Boolean = mShared.getBoolean(key, false)

    fun clearSharedPreferences() = mShared.edit(true) { clear() }

    fun getSharedPreferences(): SharedPreferences = mShared
}