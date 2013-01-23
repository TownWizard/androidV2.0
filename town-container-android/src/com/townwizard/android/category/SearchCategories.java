package com.townwizard.android.category;

import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.townwizard.android.utils.ServerConnector;
import com.townwizard.android.utils.TownWizardConstants;

public class SearchCategories extends AsyncTask<String, Category, Void> {
       
    private CategoriesAdapter categoriesAdapter;
    private Context context;    

    public SearchCategories(Context context, CategoriesAdapter categoriesAdapter) {
        this.context = context;
        this.categoriesAdapter = categoriesAdapter;
    }

    @Override
    protected void onProgressUpdate(Category ... values) {
    	super.onProgressUpdate(values);
    	categoriesAdapter.addItem(values[0]);    	
    }

    @Override
    protected Void doInBackground(String ... params) {
        try {
            String mId = params[0];
            URL url = new URL(TownWizardConstants.SECTION_API + URLEncoder.encode(mId, "UTF-8"));        
            String response = ServerConnector.getServerResponse(url);

            if (response.length() > 0) {
                JSONObject mMainJsonObject = new JSONObject(response);
                int status = mMainJsonObject.getInt("status");

                if (status == 1) {
                    JSONArray jsArr = mMainJsonObject.getJSONArray("data");
                    for (int i = 0; i < jsArr.length(); i++) {
                        JSONObject jsObject = jsArr.getJSONObject(i);
                        String name = jsObject.getString("display_name");
                        String eventUrl = jsObject.getString("url");

                        Bitmap image = Category.getImageFromResourceByName(context, name);
                        publishProgress(new Category(image, name, eventUrl));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}