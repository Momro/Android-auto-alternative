package com.example.cardashboard.settings

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SettingsStore(context: Context) {

    private val appContext = context.applicationContext

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            appContext,
            PREFS_NAME,
            MasterKey.Builder(appContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var haBaseUrl: String
        get() = prefs.getString(KEY_HA_BASE_URL, "") ?: ""
        set(value) = prefs.edit { putString(KEY_HA_BASE_URL, value.trim()) }

    var haToken: String
        get() = prefs.getString(KEY_HA_TOKEN, "") ?: ""
        set(value) = prefs.edit { putString(KEY_HA_TOKEN, value.trim()) }

    var quickActionsJson: String
        get() = prefs.getString(KEY_QUICK_ACTIONS, "") ?: ""
        set(value) = prefs.edit { putString(KEY_QUICK_ACTIONS, value) }

    companion object {
        private const val PREFS_NAME = "car_dashboard_secure"
        private const val KEY_HA_BASE_URL = "ha_base_url"
        private const val KEY_HA_TOKEN = "ha_token"
        private const val KEY_QUICK_ACTIONS = "quick_actions_json"
    }
}
