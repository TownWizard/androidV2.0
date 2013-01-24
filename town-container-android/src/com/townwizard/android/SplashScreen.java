package com.townwizard.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
    
    private static final String GENERIC_PARTNER_ID = "TownWizard";
    private static final int SPLASH_TIME = 1000;  
    
    private Handler handler;
    private Runnable runnable;
    private boolean isTownWizard;
    private Partner partner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String partnerId = getPartnerId();
        isTownWizard = (GENERIC_PARTNER_ID.equals(partnerId));

        if (!isTownWizard) {
            partner = loadPartner(partnerId);
        }
        
        setContentView(R.layout.splash);
        new CurrentLocation(this).getLocation();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                startNextActivity();
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handler.removeCallbacks(runnable);
            startNextActivity();
        }
        return true;
    }
    
    private String getPartnerId() {
        InputStream is = null;
        try {
            is = getAssets().open("params.txt");
            Properties p = new Properties();
            p.load(is);
            return p.getProperty("ID");            
        } catch (IOException e) {
            isTownWizard = true;            
            e.printStackTrace();
        } finally {
            if(is != null) try { is.close(); } catch(IOException e) { e.printStackTrace(); }
        }
        return null;
    }    
    
    private Partner loadPartner(String partnerId) {
        try {
            URL url = new URL(TownWizardConstants.PARTNER_API + partnerId);
            Log.d("Search URL = ", url.toString());

            String response = ServerConnector.getServerResponse(url);
            Log.d("JSON = ", response);

            JSONObject mMainJsonObject = new JSONObject(response);
            int status = mMainJsonObject.getInt("status");

            if (status == 1) {
                JSONObject jsObj = mMainJsonObject.getJSONObject("data");
                int id = jsObj.getInt("id");
                String name = jsObj.getString("name");                
                String androidAppId = jsObj.getString("android_app_id");
                String imageUrl = jsObj.getString("image");
                String siteUrl = jsObj.getString("website_url");                
                if (siteUrl.charAt(siteUrl.length() - 1) != '/') {
                    siteUrl += "/";
                }
                
                Partner p = new Partner(name, siteUrl, androidAppId, id, imageUrl);
                Log.d("partner", p.toString());
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
    
    private void startNextActivity() {
        if (isTownWizard) {
            startTownWizardActivity();
        } else {
            startCategoriesActivity();
        }
        finish();
    }    
    
    private void startTownWizardActivity() {        
        startActivity(new Intent(this, TownWizardActivity.class));        
    }

    private void startCategoriesActivity() {
        Intent categories = new Intent(this, CategoriesActivity.class);
        categories.putExtra(TownWizardConstants.PARTNER_NAME, partner.getName());
        categories.putExtra(TownWizardConstants.PARTNER_ID, Integer.toString(partner.getId()));
        categories.putExtra(TownWizardConstants.URL, partner.getUrl());
        categories.putExtra(TownWizardConstants.IMAGE_URL, partner.getImageUrl());
        startActivity(categories);
    }
}
