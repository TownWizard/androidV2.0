package com.townwizard.android.utils;

import android.content.Context;
import android.location.Location;

import com.townwizard.android.async.GetLocationTask;
import com.townwizard.android.async.GetLocationTask.OnLocationObtainedListener;

public class CurrentLocation implements OnLocationObtainedListener {
    public static double sLatitude = 0.0f;
    public static double sLongitude = 0.0f;
    private Context mContext;

    public CurrentLocation(Context context) {
        mContext = context;
    }
    public void getLocation(){
        new GetLocationTask(mContext, this).execute();
    }

    @Override
    public void onLocationObtained(Location location) {
        sLatitude = location.getLatitude();
        sLongitude = location.getLongitude();
    }

}
