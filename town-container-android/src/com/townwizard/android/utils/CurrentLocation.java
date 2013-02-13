package com.townwizard.android.utils;

import android.content.Context;
import android.location.Location;

import com.townwizard.android.async.GetLocationTask;
import com.townwizard.android.async.GetLocationTask.OnLocationObtainedListener;

public class CurrentLocation implements OnLocationObtainedListener {
    
    private static double latitude = 0.0f;
    private static double longitude = 0.0f;
    private static Location location;
    
    private Context context;
    
    public CurrentLocation(Context context) {
        this.context = context;
    }
    
    public void getLocation() {
        new GetLocationTask(context, this).execute();
    }

    @Override
    public void onLocationObtained(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        location = loc;
    }

    public static double latitude() {
        return latitude;
    }

    public static double longitude() {
        return longitude;
    }

    public static Location location() {
        return location;
    }

}
