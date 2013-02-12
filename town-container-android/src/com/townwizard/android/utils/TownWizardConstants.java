package com.townwizard.android.utils;

public class TownWizardConstants {
    public static final String CATEGORY_NAME = "CATEGORY NAME";
    public static final String PARTNER_ID = "PARTNER ID";
    public static final String PARTNER_NAME = "PARTNER NAME";
    public static final String URL = "URL";
    public static final String IMAGE_URL = "IMAGE URL";
    public static final String URL_SITE = "URL SITE";
    public static final String URL_SECTION = "URL SECTION";
    public static final String HEADER_IMAGE = "HEADER IMAGE";
    public static final String IMAGE_URI = "IMAGE URI";
    public static final String SOURCE = "SOURCE";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String ITEM_LOCATION = "ITEM LOCATION";
    public static final String CONTAINER_SITE="http://www.townwizardcontainerapp.com";
    public static final String PARTNER_API=CONTAINER_SITE+"/apiv30/partner/";
    public static final String SECTION_API=CONTAINER_SITE+"/apiv30/section/partner/";
    public static final String PARTNER_API_OLD=CONTAINER_SITE+"/api/partner/";
    public static final String SECTION_API_OLD=CONTAINER_SITE+"/api/section/partner/";
    public static final String DEFAULT_ABOUT_US_URI = "components/com_shines_v2.1/iphone-about.php"; 
    
    public static final boolean isTest() {
        return CONTAINER_SITE.contains("test");
    }
}