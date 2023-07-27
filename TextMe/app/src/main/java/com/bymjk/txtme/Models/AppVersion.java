package com.bymjk.txtme.Models;

public class AppVersion {

    Features features;
    long timestamp;
    int totalFeatures;
    String url;
    String version;





    public AppVersion(Features features, long timestamp, int totalFeatures, String url, String version ) {
        this.features = features;
        this.timestamp = timestamp;
        this.totalFeatures = totalFeatures;
        this.url = url;
        this.version = version;

    }

    public AppVersion() {

    }

    public String getAppVersion() {
        return version;
    }

    public String getAppUrl() {
        return url;
    }

    public long getAppUpdateTimestamp() {
        return timestamp;
    }

    public int getTotalFeatures() {
        return totalFeatures;
    }

    public Features getFeature() {
        return features;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTotalFeatures(int totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    public void setFeature(Features features) {
        this.features = features;
    }
}
