package com.mlsdev.android.townwizard.utils;

import android.content.Context;
import android.location.Location;

import com.mlsdev.android.townwizard.async.GetLocationTask;
import com.mlsdev.android.townwizard.async.GetLocationTask.OnLocationObtainedListener;

public class CurrentLocation implements OnLocationObtainedListener {
    public static double sLatitude = -1f;
    public static double sLongitude = -1f;
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
