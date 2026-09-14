package com.example.taskerclone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.example.taskerclone.data.ActionExecutor
import com.example.taskerclone.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val isConnected = wifiManager.connectionInfo?.networkId != -1
        val triggerType = if (isConnected) "WIFI_CONNECT" else "WIFI_DISCONNECT"

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(context.applicationContext).profileDao()
            val profiles = dao.getEnabledByTriggerType(triggerType)
            for (profile in profiles) {
                // triggerValue holds the target SSID; only fire if it matches (or is empty = any network)
                val currentSsid = wifiManager.connectionInfo?.ssid?.replace("\"", "") ?: ""
                if (profile.triggerValue.isBlank() || profile.triggerValue == currentSsid) {
                    ActionExecutor.execute(context.applicationContext, profile.actionType, profile.actionValue)
                }
            }
        }
    }
}
