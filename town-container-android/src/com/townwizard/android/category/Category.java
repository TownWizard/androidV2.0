package com.townwizard.android.category;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.townwizard.android.R;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.utils.ServerConnector;

public class Category implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public static enum ViewType {
        WEB,
        JSON,
        NONE;
        
        private static ViewType fromString(String name) {
            if("json".equals(name)) return JSON;
            else if("webview".equals(name)) return WEB;
            else return NONE;
        }
    }
	
    transient private final Bitmap image;
	private final String displayName; // bhavan: was "name"
	                                  // displayName is show in the menu in the app
	private final String sectionName; // bhavan: handle section and display name e.g. "fine dining"
	                                  // sectionName drives app behaviour e.g. "Restaurants"
	private final String url;
	transient private final ViewType viewType;
	
    public Category(Bitmap image, String displayname, String sectionname, String url, ViewType viewType) {
        this.image = image;
        this.displayName = handleSpecialChars(displayname);
        this.sectionName = handleSpecialChars(sectionname);
        this.url = url;
        this.viewType = viewType;
    }
    
	public Category(Bitmap image, String displayname, String sectionname, String url, String viewType) {
	    this(image, displayname, sectionname, url, ViewType.fromString(viewType));
	}
	
	public Bitmap getImage(){
		return image;
	}
	
    public String getDisplayName(){
        return displayName;
    }
    
    public String getSectionName(){
        return sectionName;
    }
	
	public String getUrl(){
		return url;
	}
	
	public ViewType getViewType() {
	    return viewType;
	}
	
    public static Bitmap getImageFromUrl(String url) throws IOException {        
        InputStream is = null;
        try {            
            is = new URL(url).openStream();
            return BitmapFactory.decodeStream(is);            
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }
    
    public static Bitmap getImageFromResourceByName(Context context, String displayname, String sectionname) {
        Integer resource = categoryToResource.get(displayname);
        if(resource == null) {
            resource = categoryToResource.get(sectionname);
            if(resource == null) {
                resource = R.drawable.icon_star;
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), resource);
    }
    
    public Class<? extends Activity> getJsonViewActivityClass() {
        return IMPLEMENTED_JSON_VIEWS.get(getSectionName());
    }
    
    public boolean hasView() {
        if(url == null) return false;
        if (ViewType.NONE.equals(getViewType())) return false;
        else if(ViewType.JSON.equals(getViewType()) && IMPLEMENTED_JSON_VIEWS.get(getSectionName()) == null)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return displayName + ": " + url; // bhavan: investigate use of this fuction
    }
    
    private static Map<String, Integer> categoryToResource = new HashMap<String, Integer>();
    static {
        categoryToResource.put(Constants.HOME, R.drawable.home);
        categoryToResource.put(Constants.NEWS, R.drawable.news);
        categoryToResource.put(Constants.EVENTS, R.drawable.events);
        categoryToResource.put(Constants.RESTAURANTS, R.drawable.restaurants);
        categoryToResource.put(Constants.PLACES, R.drawable.places);
        categoryToResource.put(Constants.PHOTOS, R.drawable.photos);
        categoryToResource.put(Constants.VIDEOS, R.drawable.videos);
        categoryToResource.put(Constants.HELP_AND_SUPPORT_AMP, R.drawable.help);
        categoryToResource.put(Constants.ADVERTISE_WITH_US, R.drawable.advertise);
        categoryToResource.put(Constants.ABOUT_US, R.drawable.about);
        categoryToResource.put(Constants.CONTACT_US, R.drawable.contact);
        categoryToResource.put(Constants.WEATHER, R.drawable.weather);
        categoryToResource.put(Constants.FACEBOOK, R.drawable.facebook);
        categoryToResource.put(Constants.GOOGLE_PLUS, R.drawable.google_plus);
        categoryToResource.put(Constants.TWITTER, R.drawable.twitter);
        categoryToResource.put(Constants.SHOPPING, R.drawable.shopping);
        categoryToResource.put(Constants.STAR, R.drawable.icon_star);
    }
    
    private static final Map<String, Class<? extends Activity>> IMPLEMENTED_JSON_VIEWS = 
            new HashMap<String, Class<? extends Activity>>();
	
	private String handleSpecialChars(String name) {
	    if(name == null || "".equals(name))
	        return null;
	    
	    if(name.contains("&amp;")) {
	        return name.replace("&amp;", "&");
	    }
	    
	    return name;
	}
	
    public static List<Category> getCategories(Context context, String partnerId) throws Exception {
        URL url = new URL(Config.SECTION_API + URLEncoder.encode(partnerId, "UTF-8"));        
        String response = ServerConnector.getServerResponse(url);

        List<Category> result = null;
        if (response.length() > 0) {
            JSONObject mMainJsonObject = new JSONObject(response);
            int status = mMainJsonObject.getInt("status");
            if (status == 1) {
                JSONArray jsArr = mMainJsonObject.getJSONArray("data");
                result = new ArrayList<Category>();
                for (int i = 0; i < jsArr.length(); i++) {
                    JSONObject jsObject = jsArr.getJSONObject(i);
                    // bhavan: String name = jsObject.getString("display_name");
                    String displayName = jsObject.getString("display_name");
                    String sectionName = jsObject.getString("section_name");
                    String categoryUrl = getCategoryUrl(jsObject);                        
                    String viewType = getViewType(jsObject);
                    Bitmap image = Category.getImageFromResourceByName(context, displayName, sectionName);
                    result.add(new Category(image, displayName, sectionName, categoryUrl, viewType));
                }
            } else {
                result = Collections.<Category>emptyList();
            }
        } else {
            result = Collections.<Category>emptyList();
        }
        return result;
    }

    private static String getCategoryUrl(JSONObject jsObject) throws JSONException {
        String categoryUrl = null;
        if(jsObject.has("android_url")) {
            categoryUrl = jsObject.getString("android_url");
        }
        if("null".equals(categoryUrl) || "".equals(categoryUrl)) categoryUrl = null;
        
        if(categoryUrl == null && Config.IS_DEV) {
            categoryUrl = jsObject.getString("url");
            if("null".equals(categoryUrl) || "".equals(categoryUrl)) categoryUrl = null;
        }
        return categoryUrl;
    }
    
    private static String getViewType(JSONObject jsObject) throws JSONException {
        return jsObject.has("android_ui_type") ? jsObject.getString("android_ui_type") : null;
    }    
}
