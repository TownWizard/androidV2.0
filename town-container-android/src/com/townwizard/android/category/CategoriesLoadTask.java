package com.townwizard.android.category;

import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.townwizard.android.config.Config;
import com.townwizard.android.utils.ServerConnector;

public class CategoriesLoadTask extends AsyncTask<String, Category, Void> {
       
    private CategoriesAdapter categoriesAdapter;
    private Context context;    

    private CategoriesLoadTask(Context context) {
        this.context = context;
        this.categoriesAdapter = new CategoriesAdapter(context);
    }
    
    public static final CategoriesAdapter loadCategories(Context context, String partnerId, boolean async) {
        CategoriesLoadTask categoryLoader = new CategoriesLoadTask(context);
        if(async) {
            categoryLoader.execute(new String[]{partnerId});
        } else {
            categoryLoader.loadCategories(partnerId);
        }
        return categoryLoader.categoriesAdapter;
    }
    
    @Override
    protected void onProgressUpdate(Category ... values) {
    	super.onProgressUpdate(values);
    	categoriesAdapter.addItem(values[0]);    	
    }

    @Override
    protected Void doInBackground(String ... params) {
        doLoadCategories(params[0], new LoadedCategoryHandler() {
            @Override
            public void handleCategory(Category c) {
                publishProgress(c);
            }
        });
        return null;
    }
    
    private void loadCategories(String partnerId) {
        doLoadCategories(partnerId, new LoadedCategoryHandler() {
            @Override
            public void handleCategory(Category c) {
                categoriesAdapter.addItem(c);
            }
        });        
    }   
    
    private void doLoadCategories(String partnerId, LoadedCategoryHandler handler) {        
        try {
            String mId = partnerId;
            URL url = new URL(Config.SECTION_API + URLEncoder.encode(mId, "UTF-8"));        
            String response = ServerConnector.getServerResponse(url);

            if (response.length() > 0) {
                JSONObject mMainJsonObject = new JSONObject(response);
                int status = mMainJsonObject.getInt("status");

                if (status == 1) {
                    JSONArray jsArr = mMainJsonObject.getJSONArray("data");
                    for (int i = 0; i < jsArr.length(); i++) {
                        JSONObject jsObject = jsArr.getJSONObject(i);
                        String name = jsObject.getString("display_name");
                        String categoryUrl = getCategoryUrl(jsObject);                        
                        String viewType = getViewType(jsObject);
                        Bitmap image = Category.getImageFromResourceByName(context, name);
                        handler.handleCategory(new Category(image, name, categoryUrl, viewType));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    private String getCategoryUrl(JSONObject jsObject) throws JSONException {
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
    
    private String getViewType(JSONObject jsObject) throws JSONException {
        return jsObject.has("android_ui_type") ? jsObject.getString("android_ui_type") : null;
    }
    
    private static interface LoadedCategoryHandler {
        void handleCategory(Category c);
    }

}