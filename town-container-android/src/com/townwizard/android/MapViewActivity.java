package com.townwizard.android;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.utils.CurrentLocation;

public class MapViewActivity extends MapActivity {
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        Header.build(this);
        
        Bundle extras = getIntent().getExtras();
        double latitude = Double.parseDouble(extras.getString(Constants.LATITUDE));        
        double longitude = Double.parseDouble(extras.getString(Constants.LONGITUDE));        
        GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));        
        
        MapView mapView = (MapView) findViewById(R.id.mapview);                
        mapView.setBuiltInZoomControls(true);
        
        Drawable marker = getResources().getDrawable(R.drawable.marker);
        MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(marker, latitude, longitude);        
        itemizedOverlay.addOverlay(new OverlayItem(point, null, null));
        mapView.getOverlays().add(itemizedOverlay);

        MapController mc = mapView.getController();
        mc.animateTo(point);
        mc.setZoom(Config.MAP_ZOOM_DEFAULT);
        mapView.invalidate();
    }

    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
        
    private class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
        
        private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
        private double latitude;
        private double longitude;
 
        public MapItemizedOverlay(Drawable marker, double latitude, double longitude) {            
            super(boundCenter(marker));            
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public void addOverlay(OverlayItem overlay) {
            mOverlays.add(overlay);
            populate();
        }

        @Override
        protected boolean onTap(int index) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(getDirectionUrl()));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");            
            MapViewActivity.this.startActivity(intent);
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

        private String getDirectionUrl() {
            return new StringBuilder("http://maps.google.com/maps?saddr=")
                        .append(CurrentLocation.latitude()).append(",")
                        .append(CurrentLocation.longitude())
                        .append("&daddr=").append(latitude).append(",").append(longitude).toString();
        }
    }    
}
