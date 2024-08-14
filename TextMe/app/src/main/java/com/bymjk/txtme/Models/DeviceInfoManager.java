package com.bymjk.txtme.Models;

import android.content.Context;

import androidx.annotation.NonNull;

public class DeviceInfoManager {
    private String staticDeviceInfo = "";
    private String  dynamicDeviceInfo = "";

    public DeviceInfoManager(Context context) {
        this.staticDeviceInfo = new StaticDeviceInfo(context).getStaticDeviceInfo();
        this.dynamicDeviceInfo = new DynamicDeviceInfo(context).getDynamicDeviceInfo();
    }

    public String getStaticDeviceInfo() {
        return staticDeviceInfo;
    }

    public String getDynamicDeviceInfo() {
        return dynamicDeviceInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceInfoManager{" +
                "staticDeviceInfo=" + staticDeviceInfo +
                ", dynamicDeviceInfo=" + dynamicDeviceInfo +
                '}';
    }

}

