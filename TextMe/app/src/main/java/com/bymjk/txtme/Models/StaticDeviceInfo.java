package com.bymjk.txtme.Models;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.Gson;

import java.util.Locale;

public class StaticDeviceInfo {
    private String staticDeviceInfo = "";

    public StaticDeviceInfo(Context context) {
        // Collecting static device info and serializing it into a string
        StringBuilder infoBuilder = new StringBuilder();

        // Manufacturer
        String manufacturer = Build.MANUFACTURER;
        infoBuilder.append(manufacturer).append("|");

        // Device Model
        String deviceModel = Build.MODEL;
        infoBuilder.append(deviceModel).append("|");

        // OS Version
        String osVersion = Build.VERSION.RELEASE;
        infoBuilder.append(osVersion).append("|");

        // SDK Version
        int sdkVersion = Build.VERSION.SDK_INT;
        infoBuilder.append(sdkVersion).append("|");

        // Device Name
        String deviceName = Settings.Global.getString(context.getContentResolver(), "device_name");
        infoBuilder.append(deviceName != null ? deviceName : "").append("|");

        // Build Number
        String buildNumber = Build.DISPLAY;
        infoBuilder.append(buildNumber).append("|");

        // Locale
        String locale = Locale.getDefault().toString();
        infoBuilder.append(locale);

        // Assign the collected data to staticDeviceInfo
        this.staticDeviceInfo = infoBuilder.toString();
    }

    public String getStaticDeviceInfo() {
        return staticDeviceInfo;
    }

    @Override
    public String toString() {
        return "StaticDeviceInfo{" +
                "staticDeviceInfo='" + staticDeviceInfo + '\'' +
                '}';
    }
}


