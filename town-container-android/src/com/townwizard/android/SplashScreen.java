package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.google.analytics.tracking.android.EasyTracker;
import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.CategoriesLoadTask;
import com.townwizard.android.config.Config;
import com.townwizard.android.utils.CurrentLocation;
import com.townwizard.android.utils.Utils;

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
        config.loadPartnerForPartnerApp();
        
        if (!config.isContainerApp()) {
            if(Utils.isOnline(this)) {
                categoriesAdapter = CategoriesLoadTask.loadCategories(this, config.getPartnerId(), true);
            }
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
    
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
    
    private void startNextActivity() {
        if (Config.getConfig(this).isContainerApp()) {
            startActivity(new Intent(this, TownWizardActivity.class));
        } else {
            Intent web = new Intent(this, WebActivity.class);
            if(categoriesAdapter != null) {
                Config.getConfig(this).setCategory(categoriesAdapter.getHomeCategory());
            }
            startActivity(web);
        }
        finish();
    }    
}