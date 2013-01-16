package com.townwizard.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.townwizard.android.R;
import com.townwizard.android.model.Partner;
import com.townwizard.android.utils.CurrentLocation;
import com.townwizard.android.utils.ServerConnector;
import com.townwizard.android.utils.TownWizardConstants;

public class SplashScreen extends Activity{
    private int mSplashTime = 3000;
    private Handler mHandler;
    private Runnable mRunnable;
    private URL mUrl;
    private int mStatus;
    private String mPartnersName;
    private boolean isTownWizard = false;
    private Partner mPartner;
    private CurrentLocation mCurrentLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetManager am = getAssets();
        try {
            InputStream is = null;
            is = am.open("params.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("ID")) {
                    mPartnersName = line.replace("ID=", "");
                    if (mPartnersName.equals("TownWizard")) {
                        isTownWizard = true;
                    }
                }
            }
            is.close();
        } catch (IOException e) {
            isTownWizard = true;
            e.printStackTrace();
        }

        if (!isTownWizard) {
            executeSearch();
        }
        setContentView(R.layout.splash);
        mCurrentLocation = new CurrentLocation(this);
        mCurrentLocation.getLocation();

        mHandler = new Handler();
        mRunnable = new Runnable() {

            @Override
            public void run() {
                startMainScreen();
                finish();
            }
        };
        mHandler.postDelayed(mRunnable, mSplashTime);
    }

    private void executeSearch() {
        try {
            mUrl = new URL(TownWizardConstants.CONTAINER_SITE_API + URLEncoder.encode(mPartnersName));
            Log.d("Search URL = ", mUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            String response = ServerConnector.getServerResponse(mUrl);
            Log.d("JSON = ", response);

            JSONObject mMainJsonObject = new JSONObject(response);
            mStatus = mMainJsonObject.getInt("status");

            if (mStatus == 1) {

                    JSONObject jsObj = mMainJsonObject.getJSONObject("data");
                    int partnerId = jsObj.getInt("id");
                    String name = jsObj.getString("name");
                    String url = jsObj.getString("website_url");
                    String androidAppId = jsObj.getString("android_app_id");
                    String imageUrl = jsObj.getString("image");
                    Log.d("partner_id", Integer.toString(partnerId));
                    Log.d("name", name);
                    Log.d("url", url);

                    if (url.charAt(url.length() - 1) != '/') {
                        url += "/";
                    }
                    mPartner = new Partner(name, url, androidAppId, partnerId, imageUrl);
                    Log.d("app_id", androidAppId);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void startMainScreen() {
        if (isTownWizard) {
            Intent i = new Intent(this, TownWizardActivity.class);
            startActivity(i);
        } else {
            startCategoriesActivity();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(mRunnable);
            if (isTownWizard) {
                startMainScreen();
            } else {
                startCategoriesActivity();
            }
            finish();
        }
        return true;
    }

    private void startCategoriesActivity() {
        Intent categories = new Intent(this, CategoriesActivity.class);
        categories.putExtra(TownWizardConstants.PARTNER_NAME, mPartner.getName());
        categories.putExtra(TownWizardConstants.PARTNER_ID, Integer.toString(mPartner.getPartnerId()));
        categories.putExtra(TownWizardConstants.URL, mPartner.getUrl());
        if (mPartner.getImageUrl().length() > 0) {
            categories.putExtra(TownWizardConstants.IMAGE_URL, mPartner.getImageUrl());
        } else {
            categories.putExtra(TownWizardConstants.IMAGE_URL, "");
        }
        startActivity(categories);
    }
}
