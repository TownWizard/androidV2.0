package com.townwizard.android.category;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.townwizard.android.R;
import com.townwizard.android.config.Constants;

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
	private final String name;
	private final String url;
	transient private final ViewType viewType;
	
    public Category(Bitmap image, String name, String url, ViewType viewType) {
        this.image = image;
        this.name = handleSpecialChars(name);
        this.url = url;
        this.viewType = viewType;
    }
    
	public Category(Bitmap image, String name, String url, String viewType) {
	    this(image, name, url, ViewType.fromString(viewType));
	}
	
	public Bitmap getImage(){
		return image;
	}
	
	public String getName(){
		return name;
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
    
    public static Bitmap getImageFromResourceByName(Context context, String categoryName) {
        Integer resource = categoryToResource.get(categoryName);
        if(resource == null) {
            resource = R.drawable.icon_star;
        }
        return BitmapFactory.decodeResource(context.getResources(), resource);
    }
    
    public Class<? extends Activity> getJsonViewActivityClass() {
        return IMPLEMENTED_JSON_VIEWS.get(getName());
    }
    
    public boolean hasView() {
        if(url == null) return false;
        if (ViewType.NONE.equals(getViewType())) return false;
        else if(ViewType.JSON.equals(getViewType()) && IMPLEMENTED_JSON_VIEWS.get(getName()) == null)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return name + ": " + url;
    }
    
    private static Map<String, Integer> categoryToResource = new HashMap<String, Integer>();
    static {
        categoryToResource.put(Constants.HOME, R.drawable.home);
        categoryToResource.put(Constants.NEWS, R.drawable.home);
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
}
