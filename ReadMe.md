# TaskerClone

Personal automation app for Android — inspired by Tasker. Define **Profiles**
(trigger + action pairs) that run automatically in the background.

## Features (current MVP)

**Triggers:** TIME, WIFI_CONNECT, WIFI_DISCONNECT, CHARGING_START, CHARGING_STOP,
BATTERY_LOW, BATTERY_OKAY

**Actions:** NOTIFY, LAUNCH_APP, SET_VOLUME, TOGGLE_WIFI, VIBRATE, TORCH_ON, TORCH_OFF

## How to build (no Android Studio needed)

This repo builds automatically via **GitHub Actions**:

1. Push any change to the `main` branch (or go to the "Actions" tab and run
   the "Build APK" workflow manually).
2. Wait for the workflow to finish (green checkmark).
3. Open the finished run → scroll to **Artifacts** → download `app-debug-apk`.
4. Unzip it, transfer `app-debug.apk` to your phone, and install it
   (allow "install from unknown sources" since this isn't from Play Store).

## Project structure

```
app/src/main/java/com/example/taskerclone/
  data/      -> Room database, entities, DAO, ActionExecutor (runs actions)
  service/   -> AutomationService (foreground service, polls TIME triggers)
  receiver/  -> BroadcastReceivers (Boot, Battery, WiFi triggers)
  ui/        -> MainActivity (add/view profiles)
```

## Adding a new trigger type

1. Add handling in the relevant `BroadcastReceiver` (or `AutomationService`
   for polling-based checks), matching a new `triggerType` string.
2. Use that trigger type string when creating a Profile from the UI.

## Adding a new action type

1. Add a new `when` branch in `ActionExecutor.kt`.
2. Use that action type string when creating a Profile from the UI.

## Important: keeping it running reliably

After installing, go to your phone's Settings → Apps → TaskerClone → Battery
and disable battery optimization for this app, otherwise Android may kill
the background service over time.
