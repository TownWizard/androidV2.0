package com.townwizard.android.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.townwizard.android.R;

public final class CategoryImage {    
        
    private CategoryImage() {}
    
    public static Bitmap getFromUrl(String url) throws IOException {        
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
    
    public static Bitmap getFromResourceByName(Context context, String categoryName) {
        Integer resource = categoryToResource.get(categoryName);
        if(resource == null) {
            resource = R.drawable.icon_star;
        }
        return BitmapFactory.decodeResource(context.getResources(), resource);
    }
    
    private static Map<String, Integer> categoryToResource = new HashMap<String, Integer>();
    static {
        /*
        @"news feed" : @"news",
        @"offers" : @"offer",
        @"nightlife" : @"nightlife",
        @"entertainment" : @"entertainment",
        @"town dirrectory" : @"towndirrectory",
        @"your profile" : @"profile",            -> photos
        @"your saved items" : @"saved",
        @"settings & preferences" : @"settings",
        @"best in town lists" : @"bestintown",
        @"talk of the town blog" : @"talk",
        @"ratings & reviews" : @"ratings",
        @"check-ins & hotspots" : @"checkins",
         */
        categoryToResource.put("Home", R.drawable.home);
        categoryToResource.put("Events", R.drawable.events);
        categoryToResource.put("Restaurants", R.drawable.restaurants);
        categoryToResource.put("Places", R.drawable.places);
        categoryToResource.put("Photos", R.drawable.photos);
        categoryToResource.put("Videos", R.drawable.videos);
        categoryToResource.put("Help & amp; Support", R.drawable.help);
        categoryToResource.put("Advertise with Us", R.drawable.advertise);
        categoryToResource.put("About Us", R.drawable.about);
        categoryToResource.put("Contact Us", R.drawable.contact);
        categoryToResource.put("Weather", R.drawable.weather);
    }
}