package com.townwizard.android.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.townwizard.android.MapViewActivity;

public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;

    public MapItemizedOverlay(Drawable defaultMarker, Context context) {
	super(boundCenter(defaultMarker));
	mContext = context;
    }

    @Override
    protected boolean onTap(int index) {

	Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		Uri.parse("http://maps.google.com/maps?saddr="+CurrentLocation.latitude()+","+CurrentLocation.longitude()+"&daddr="+
			MapViewActivity.sLatitude+","+MapViewActivity.sLongitude));
	intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
	mContext.startActivity(intent);

	return true;
    }

    @Override
    protected OverlayItem createItem(int i) {
	return mOverlays.get(i);
    }

    @Override
    public int size() {
	return mOverlays.size();
    }

    public void addOverlay(OverlayItem overlay) {
	mOverlays.add(overlay);
	populate();
    }

}
