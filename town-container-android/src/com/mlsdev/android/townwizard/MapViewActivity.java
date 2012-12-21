package com.mlsdev.android.townwizard;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mlsdev.android.townwizard.R;
import com.mlsdev.android.townwizard.utils.MapItemizedOverlay;

public class MapViewActivity extends MapActivity {
	private List<Overlay> mMapOverlays;
	private ImageView mImageView;
	private TextView mTextView;
	public static float sLatitude;
	public static float sLongitude;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    Bundle extras = getIntent().getExtras();
		Bitmap bitmap = extras.getParcelable("HEADER_IMAGE");
		mImageView = (ImageView) findViewById(R.id.iv_header_map);
		mImageView.setImageBitmap(bitmap);
		mTextView = (TextView) findViewById(R.id.tv_header_map);
		mTextView.setText(extras.getString("PARTNER_NAME"));
	    mapView.setBuiltInZoomControls(true);
	    mMapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	    MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(drawable, this);
	    //Bundle extras = getIntent().getExtras();
	    String latitude = extras.getString("LATITUDE");
	    sLatitude = Float.parseFloat(latitude);
	    String longitude = extras.getString("LONGITUDE");
	    sLongitude = Float.parseFloat(longitude);
	    GeoPoint point = new GeoPoint((int)(sLatitude * 1E6), (int)(sLongitude * 1E6));
	    OverlayItem overlayitem = new OverlayItem(point, "", null);
	    itemizedoverlay.addOverlay(overlayitem);
	    mMapOverlays.add(itemizedoverlay);
	    
	    MapController mc = mapView.getController();
        mc.animateTo(point);
        mc.setZoom(15);
        mapView.invalidate();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
