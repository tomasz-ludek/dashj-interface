package org.dashj.dashjinterface.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class MainPreferences(context: Context) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var fullSyncComplete: Boolean = false
        get() = fullSyncDate > 0

    var fullSyncDate: Long
        get() = preferences.getLong(Contract.KEY_FULL_SYNC_DATE, 0)
        set(value) = save(Contract.KEY_FULL_SYNC_DATE, value)

    private fun save(key: String, value: Any) {
        val editor = preferences.edit()
        when (value) {
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    object Contract {
        internal const val KEY_FULL_SYNC_DATE = "key_full_sync_date"
    }
}