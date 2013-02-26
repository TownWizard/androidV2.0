package com.townwizard.android;

import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.CategoriesLoadTask;
import com.townwizard.android.config.Config;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.utils.CurrentLocation;
import com.townwizard.android.utils.ServerConnector;

/**
 * Application entry activity.  Shows a splash screen, and starts geolocation, then
 * redirects to the home page.
 */
public class SplashScreen extends Activity {

    private Handler handler;
    private Runnable runnable;    
    private CategoriesAdapter categoriesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        new CurrentLocation(this).getLocation();
        
        Config config = Config.getConfig(this);        
        if (!config.isContainerApp()) {
            String partnerId = config.getPartnerId();
            Config.getConfig(this).setPartner(loadPartner(partnerId));
            categoriesAdapter = CategoriesLoadTask.loadCategories(this, partnerId);
        }
        
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                startNextActivity();
            }
        };
        handler.postDelayed(runnable, Config.SPLASH_TIME);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handler.removeCallbacks(runnable);
            startNextActivity();
        }
        return true;
    }
    
    private Partner loadPartner(String partnerId) {
        try {
            URL url = new URL(Config.PARTNER_API + partnerId);
            String response = ServerConnector.getServerResponse(url);
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
                
                return new Partner(name, siteUrl, androidAppId, id, imageUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
    
    private void startNextActivity() {
        if (Config.getConfig(this).isContainerApp()) {
            startActivity(new Intent(this, TownWizardActivity.class));
        } else {
            Intent web = new Intent(this, WebActivity.class);
            Config.getConfig(this).setCategory(categoriesAdapter.getHomeCategory());
            startActivity(web);
        }
        finish();
    }    
}