package com.bymjk.txtme.Models;

import androidx.annotation.NonNull;

public class DeviceInfoDataConvention {
    private final String locationDataTypes = "Provider|Time|ElapsedRealtimeNanos|Latitude|Longitude|Altitude|Speed|Bearing|Accuracy|ElapsedRealtimeUncertaintyNanos|VerticalAccuracyMeters|SpeedAccuracyMetersPerSecond|BearingAccuracyDegrees|Extras";
    private final String staticDeviceInfoTypes = "manufacturer|deviceModel|osVersion|sdkVersion|deviceName|buildNumber|locale";
    private final String dynamicDeviceInfoTypes = "batteryLevel|networkType|signalStrength|screenBrightness|isBluetoothEnabled|isGPSEnabled|isHotspotEnabled|isDarkModeEnabled|isSilentMode|mediaVolume|ringtoneVolume|isBatterySaverEnabled|isAirplaneModeEnabled";

    public String getLocationDataTypes() {
        return locationDataTypes;
    }

    public String getStaticDeviceInfoTypes() {
        return staticDeviceInfoTypes;
    }

    public String getDynamicDeviceInfoTypes() {
        return dynamicDeviceInfoTypes;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceInfoDataConvention{" +
                "locationDataTypes='" + locationDataTypes + '\'' +
                ", staticDeviceInfoTypes='" + staticDeviceInfoTypes + '\'' +
                ", dynamicDeviceInfoTypes='" + dynamicDeviceInfoTypes + '\'' +
                '}';
    }
}
