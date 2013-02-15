package com.townwizard.android.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Application;
import android.content.Context;

public final class Config extends Application {
    
    public static final String CONTAINER_SITE="http://www.townwizardcontainerapptest.com";
    public static final String PARTNER_API=CONTAINER_SITE+"/apiv30/partner/";
    public static final String SECTION_API=CONTAINER_SITE+"/apiv30/section/partner/";
    public static final String DEFAULT_ABOUT_US_URI = "components/com_shines_v2.1/iphone-about.php";
    public static final String DEFAULT_HOME_URI = "components/com_shines_v2.1/iphone-30a-today.php";
    public static final int SPLASH_TIME = 1000;
    public static final int FB_CHECKIN_DISTANCE_METERS = 2000;
    public static final int FB_CHECKIN_RESULTS_LIMIT = 20;
    
    private static final String GENERIC_PARTNER_ID = "TownWizard";
    private static final String PARAMS_FILE = "params.txt";

    private boolean test = CONTAINER_SITE.contains("test"); 
    private String partnerId;
    private boolean containerApp;
        
    public static Config getConfig(Context context) {
        return (Config)context.getApplicationContext();
    }
    
    @Override
    public void onCreate() {
        partnerId = loadPartnerId();
        containerApp = (GENERIC_PARTNER_ID.equals(partnerId));
    }

    public boolean isTest() {
        return test;
    }
    
    public boolean isContainerApp() {
        return containerApp;
    }
    
    public String getPartnerId() {
        return partnerId;
    }

    private String loadPartnerId() {
        InputStream is = null;
        try {
            is = getAssets().open(PARAMS_FILE);
            Properties p = new Properties();
            p.load(is);
            return p.getProperty("ID");            
        } catch (IOException e) {            
            e.printStackTrace();
        } finally {
            if(is != null) try { is.close(); } catch(IOException e) { e.printStackTrace(); }
        }
        return null;
    } 
}
