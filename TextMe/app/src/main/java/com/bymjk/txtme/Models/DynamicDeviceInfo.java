package com.bymjk.txtme.Models;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;

public class DynamicDeviceInfo {
    private String dynamicDeviceInfo;

    public DynamicDeviceInfo(Context context) {
        // Collecting dynamic device info and serializing it into a string
        StringBuilder infoBuilder = new StringBuilder();

        // Get battery level
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        infoBuilder.append(batteryLevel).append("|");

        // Get network type and signal strength
        String networkType = "Unknown";
        int signalStrength = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    networkType = "WiFi";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    networkType = "Cellular";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signalStrength = capabilities.getSignalStrength();
                } else {
                    signalStrength = 0;
                }
            } else {
                networkType = "Not Connected";
                signalStrength = 0;
            }
        }
        infoBuilder.append(networkType).append("|").append(signalStrength).append("|");

        // Get screen brightness
        int screenBrightness = 0;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            screenBrightness = -1;
        }
        infoBuilder.append(screenBrightness).append("|");

        // Get Bluetooth status
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isBluetoothEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        infoBuilder.append(isBluetoothEnabled ? 1 : 0).append("|");

        // Get GPS status
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        infoBuilder.append(isGPSEnabled ? 1 : 0).append("|");

        // Get Hotspot status
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isHotspotEnabled = wifiManager != null && wifiManager.isWifiEnabled(); // Assuming Wi-Fi hotspot is enabled if Wi-Fi is on
        infoBuilder.append(isHotspotEnabled ? 1 : 0).append("|");

        // Get Dark Mode status
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkModeEnabled = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
        infoBuilder.append(isDarkModeEnabled ? 1 : 0).append("|");

        // Get Silent/Mute status
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isSilentMode = audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
        infoBuilder.append(isSilentMode ? 1 : 0).append("|");

        // Get Media and Ringtone Volume
        int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int ringtoneVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        infoBuilder.append(mediaVolume).append("|").append(ringtoneVolume).append("|");

        // Get Battery Saver status
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isBatterySaverEnabled = powerManager.isPowerSaveMode();
        infoBuilder.append(isBatterySaverEnabled ? 1 : 0).append("|");

        // Get Airplane Mode status
        boolean isAirplaneModeEnabled = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        infoBuilder.append(isAirplaneModeEnabled ? 1 : 0);

        // Assign the collected data to dynamicDeviceInfo
        this.dynamicDeviceInfo = infoBuilder.toString();
    }

    public String getDynamicDeviceInfo() {
        return dynamicDeviceInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return "DynamicDeviceInfo{" +
                "dynamicDeviceInfo='" + dynamicDeviceInfo + '\'' +
                '}';
    }
}



