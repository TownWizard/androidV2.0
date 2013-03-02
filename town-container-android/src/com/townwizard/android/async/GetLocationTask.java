package com.townwizard.android.async;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * A task, that obtains user location using certain algorithm. 1. First it
 * checks gps and network providers for availability. 2. Location updates are
 * requested from available providers. 3. Task is waiting for
 * GET_LAST_LOCATION_DELAY milliseconds to get location from available
 * providers. If a location is obtained during that time, that location is
 * returned to task caller class and location updates are removed from
 * LocationManager. 4. If location was not obtained during that time, last known
 * location is taken from the available providers. If both providers are
 * available, the latest location is taken. 5. If no location was obtained, task
 * returns an error to the caller class.
 * 
 * @author Artur Termenji, MLS-Automatization
 */
public class GetLocationTask extends AsyncTask<Void, String, Void> {

	private static final String TAG = "GetLocationTask";
	private static final String TASK_PROGRESS = " ???????????????????????? ????????????????????????????...";

	/** GPS could take more time to get fix. */
	private static final long GET_LAST_LOCATION_DELAY = 60000;

	private LocationManager mLocationManager;
	private OnLocationObtainedListener mLocationObtainedListener;
	private List<GpsNetworkLocationListener> mLocationListeners;

	private boolean mGpsEnabled;
	private boolean mNetworkEnabled;

	private Location mLocation = null;

	public GetLocationTask(Context context, OnLocationObtainedListener listener) {
		mLocationObtainedListener = listener;
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListeners = new ArrayList<GpsNetworkLocationListener>();

		initLocationListeners();
		Log.d(TAG, "Started init location...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		publishProgress(TASK_PROGRESS);

		if (mGpsEnabled || mNetworkEnabled) {
			long locationCount = 0;
			while (mLocation == null && locationCount < GET_LAST_LOCATION_DELAY) {
				locationCount += 100;
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {
					Log.e(TAG, "get last location delay interrupted", ex);
				}
			}

			if (mLocation == null)
				getLastLocation();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (mLocation != null)
			mLocationObtainedListener.onLocationObtained(mLocation);
		super.onPostExecute(result);
	}

	public interface OnLocationObtainedListener {

		public void onLocationObtained(Location location);

	}

	private void initLocationListeners() {
		getLastLocation();
		
		try {
			mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			Log.e(TAG, "Can not access gps provider.", ex);
		}
		try {
			mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			Log.e(TAG, "Can not access network provider.", ex);
		}

		if (!mGpsEnabled && !mNetworkEnabled) {
		    if(!mGpsEnabled) {
		        Log.d(TAG, "GPS location not enabled");
		    }
		    if(!mNetworkEnabled) {
		        Log.d(TAG, "Network location not enabled");
		    }
			return;
		}

		if (mGpsEnabled) {
			GpsNetworkLocationListener gpsListener = new GpsNetworkLocationListener();
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
			mLocationListeners.add(gpsListener);
		}
		if (mNetworkEnabled) {
			GpsNetworkLocationListener networkListener = new GpsNetworkLocationListener();
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
			mLocationListeners.add(networkListener);
		}
	}

	private void getLastLocation() {
		for (GpsNetworkLocationListener listener : mLocationListeners) {
			mLocationManager.removeUpdates(listener);
		}

		Location networkLocation = null, gpsLocation = null;
		if (mGpsEnabled) {
			gpsLocation = mLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		
		if (mNetworkEnabled) {
			networkLocation = mLocationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		// if there are both values use the latest one
		if (gpsLocation != null && networkLocation != null) {
			if (gpsLocation.getTime() > networkLocation.getTime()) {
				mLocation = gpsLocation;
				Log.d(TAG, "GPS location set");
			}
			else {
				mLocation = networkLocation;
				Log.d(TAG, "Network location set");
			}
		} else if (gpsLocation != null) {
			mLocation = gpsLocation;
			Log.d(TAG, "GPS location set");
		} else if (networkLocation != null) {
			mLocation = networkLocation;
			Log.d(TAG, "Network location set");
		} else {
			mLocation = null;
			Log.d(TAG, "Location is null");
		}
	}

	private class GpsNetworkLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			for (GpsNetworkLocationListener listener : mLocationListeners) {
				mLocationManager.removeUpdates(listener);
			}

			mLocation = location;
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
