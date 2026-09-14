package com.example.taskerclone.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A "Profile" = one trigger condition + one action, similar to Tasker's Profile+Task pair.
 *
 * triggerType examples: "TIME", "WIFI_CONNECT", "WIFI_DISCONNECT", "BATTERY_LEVEL",
 *                        "APP_OPEN", "CHARGING_START", "CHARGING_STOP"
 * triggerValue: extra data needed for the trigger (e.g. "22:00" for TIME, "MyHomeWifi" for WIFI,
 *               "20" for BATTERY_LEVEL, "com.whatsapp" for APP_OPEN)
 *
 * actionType examples: "NOTIFY", "LAUNCH_APP", "SET_VOLUME", "TOGGLE_WIFI", "VIBRATE",
 *                       "TORCH_ON", "TORCH_OFF", "SET_BRIGHTNESS"
 * actionValue: extra data needed for the action (e.g. notification text, app package name)
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val triggerType: String,
    val triggerValue: String,
    val actionType: String,
    val actionValue: String,
    val enabled: Boolean = true
)
