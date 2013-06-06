package com.townwizard.android.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import org.json.JSONObject;

import android.app.Application;
import android.content.Context;

import com.townwizard.android.category.Category;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.utils.JsonUtils;
import com.townwizard.android.utils.ServerConnector;
import com.townwizard.android.utils.Utils;

public final class Config extends Application {
    
    public static final String CONTAINER_SITE="http://www.townwizardcontainerapp.com";
    public static final String PARTNER_API=CONTAINER_SITE+"/apiv30/partner/";
    public static final String SECTION_API=CONTAINER_SITE+"/apiv30/section/partner/";
    public static final String DEFAULT_ABOUT_US_URI = "components/com_shines_v2.1/iphone-about.php";
    public static final String DEFAULT_HOME_URI = "components/com_shines_v2.1/iphone-30a-today.php";
    public static final int SPLASH_TIME = 500;
    public static final int FB_CHECKIN_DISTANCE_METERS = 2000;
    public static final int FB_CHECKIN_RESULTS_LIMIT = 20;
    public static final int MAP_ZOOM_DEFAULT = 15;
    public static final boolean IS_DEV = CONTAINER_SITE.contains("test");
    
    public static final String CONTENT_PARTNER_NAME = "TownWizard Auto Content";
    public static final String CONTENT_PARTNER_CONTENT_FOLDER = "twcontent";
    public static final String CONTENT_PARTNER_ZIP_CODE_URL = CONTENT_PARTNER_CONTENT_FOLDER + "/auto-zip.php";
    
    public static final String TOWNWIZARD_PHONE = "18666510001";
    
    private static final String GENERIC_PARTNER_ID = "TownWizard";
    private static final String PARAMS_FILE = "params.txt";
    private static final String PARTNER_CACHE_FILE = "partner";
    private static final String CATEGORY_CACHE_FILE = "category";
    
    private String partnerId;
    private boolean containerApp;    
    
    private Partner partner;
    private Category category;
    private String zip;
        
    public static Config getConfig(Context context) {
        return (Config)context.getApplicationContext();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        partnerId = loadPartnerId();
        containerApp = GENERIC_PARTNER_ID.equals(partnerId);
    }

    public boolean isContainerApp() {
        return containerApp;
    }
    
    public String getPartnerId() {
        return partnerId;
    }

    public Partner getPartner() {
        if(partner == null) {
            restoreApplicationData();
        }
        if(partner == null) {
            loadPartnerForPartnerApp();
        }
        return partner;
    }
    
    public void setPartner(Partner partner) {
        this.partner = partner;
        if(partner != null) {
            Locale partnerLocale = partner.getLocale();
            Locale locale = partnerLocale != null ? partnerLocale : Locale.getDefault();
            Utils.setLocale(locale, getBaseContext());
        }
    }
    
    public Category getCategory() {
        if(category == null) {
            restoreApplicationData();
        }
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public String getZip() {
        return zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    public void cacheApplicationData() {
        if(partner != null) {
            Utils.serialize(partner, getPartnerCachFile());
        }
        if(category != null) {
            Utils.serialize(category, getCategoryCachFile());
        }
    }
    
    public void loadPartnerForPartnerApp() {
        if(isContainerApp()) return;
        if(!Utils.isOnline(getApplicationContext())) return;
        
        try {
            URL url = new URL(Config.PARTNER_API + partnerId);
            String response = ServerConnector.getServerResponse(url);
            JSONObject mainJson = new JSONObject(response);
            Partner p = JsonUtils.jsonToPartner(mainJson);
            if(p != null) {
                setPartner(p);
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    private void restoreApplicationData() {
        File pFile = getPartnerCachFile();
        File cFile = getCategoryCachFile();
        
        try {
            if(pFile.exists()) {
                setPartner((Partner)Utils.deserialize(pFile));
            }
            if(cFile.exists()) {
                setCategory((Category)Utils.deserialize(cFile));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        pFile.delete();
        cFile.delete();
    }

    private File getPartnerCachFile() {        
        return new File(getCacheDir(), PARTNER_CACHE_FILE);
    }
    
    private File getCategoryCachFile() {
        return new File(getCacheDir(), CATEGORY_CACHE_FILE);
    }
    
}
