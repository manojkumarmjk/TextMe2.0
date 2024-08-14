package com.bymjk.txtme.Models;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class UserLoc {
    private String location = ""; // Serialized Location object
    private long timestamp = 0;

    // Default constructor
    public UserLoc() {}

    // Constructor with parameters
    public UserLoc(Location location, long timestamp) {
        this.location = serializeLocation(location);
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    // Method to serialize Location object to String
    private String serializeLocation(Location location) {
        if (location == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(location.getProvider() != null ? location.getProvider() : "").append("|");
        sb.append(location.getTime() != 0 ? location.getTime() : 0).append("|");
        sb.append(location.getElapsedRealtimeNanos()).append("|");
        sb.append(location.getLatitude() != 0.0 ? location.getLatitude() : 0.0).append("|");
        sb.append(location.getLongitude() != 0.0 ? location.getLongitude() : 0.0).append("|");
        sb.append(location.getAltitude() != 0.0 ? location.getAltitude() : 0.0).append("|");
        sb.append(location.getSpeed() != 0.0f ? location.getSpeed() : 0.0f).append("|");
        sb.append(location.getBearing() != 0.0f ? location.getBearing() : 0.0f).append("|");
        sb.append(location.getAccuracy() != 0.0f ? location.getAccuracy() : 0.0f).append("|");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sb.append(location.getElapsedRealtimeUncertaintyNanos() != 0.0 ? location.getElapsedRealtimeUncertaintyNanos() : 0.0).append("|");
            sb.append(location.getVerticalAccuracyMeters() != 0.0f ? location.getVerticalAccuracyMeters() : 0.0f).append("|");
            sb.append(location.getSpeedAccuracyMetersPerSecond() != 0.0f ? location.getSpeedAccuracyMetersPerSecond() : 0.0f).append("|");
            sb.append(location.getBearingAccuracyDegrees() != 0.0f ? location.getBearingAccuracyDegrees() : 0.0f).append("|");
        }
        sb.append(location.getExtras() != null ? location.getExtras().toString() : "");

        return sb.toString();
    }

    // Method to deserialize String to Location object
    public Location deserializeLocation(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return null;
        }

        try {
            String[] parts = locationString.split("\\|");

            Location location = new Location(parts[0]);
            location.setTime(Long.parseLong(parts[1]));
            location.setElapsedRealtimeNanos(Long.parseLong(parts[2]));
            location.setLatitude(Double.parseDouble(parts[3]));
            location.setLongitude(Double.parseDouble(parts[4]));
            location.setAltitude(Double.parseDouble(parts[5]));
            location.setSpeed(Float.parseFloat(parts[6]));
            location.setBearing(Float.parseFloat(parts[7]));
            location.setAccuracy(Float.parseFloat(parts[8]));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                location.setElapsedRealtimeUncertaintyNanos(Double.parseDouble(parts[9]));
                location.setVerticalAccuracyMeters(Float.parseFloat(parts[10]));
                location.setSpeedAccuracyMetersPerSecond(Float.parseFloat(parts[11]));
                location.setBearingAccuracyDegrees(Float.parseFloat(parts[12]));
            }

            // Handling extras
            if (parts.length > 13 && !parts[13].isEmpty()) {
                Bundle extras = new Bundle();
                extras.putString("extras", parts[13]);
                location.setExtras(extras);
            }

            return location;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @NonNull
    @Override
    public String toString() {
        if (location == null){
            location = "";
        }

        return "UserLoc{" +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

