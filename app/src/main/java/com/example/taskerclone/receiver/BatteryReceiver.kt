package com.example.taskerclone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskerclone.data.ActionExecutor
import com.example.taskerclone.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val triggerType = when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> "CHARGING_START"
            Intent.ACTION_POWER_DISCONNECTED -> "CHARGING_STOP"
            Intent.ACTION_BATTERY_LOW -> "BATTERY_LOW"
            Intent.ACTION_BATTERY_OKAY -> "BATTERY_OKAY"
            else -> return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(context.applicationContext).profileDao()
            val profiles = dao.getEnabledByTriggerType(triggerType)
            for (profile in profiles) {
                ActionExecutor.execute(context.applicationContext, profile.actionType, profile.actionValue)
            }
        }
    }
}
