package com.townwizard.android.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.townwizard.android.model.Category;
import com.townwizard.android.model.CategoryImage;
import com.townwizard.android.ui.adapter.CategoriesAdapter;

public class SearchCategories extends AsyncTask<String, Category, Void> {
       
    private CategoriesAdapter mCategoriesAdapter;
    private Context context;
    private int mStatus;

    public SearchCategories(Context context, CategoriesAdapter categoriesAdapter) {
        this.context = context;
        mCategoriesAdapter = categoriesAdapter;
    }

    @Override
    protected void onProgressUpdate(Category... values) {
	super.onProgressUpdate(values);
	mCategoriesAdapter.addItem(values[0]);
	mCategoriesAdapter.notifyDataSetChanged();
    }

    @Override
    protected Void doInBackground(String... params) {
	String mId = params[0];
	URL url = null;
	try {
	    url = new URL(TownWizardConstants.SECTION_API + URLEncoder.encode(mId, "UTF-8"));
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	try {
	    String response = ServerConnector.getServerResponse(url);
	    
	    if (response.length() > 0) {

		JSONObject mMainJsonObject = new JSONObject(response);
		mStatus = mMainJsonObject.getInt("status");

		if (mStatus == 1) {
		    JSONArray jsArr = mMainJsonObject.getJSONArray("data");
		    for (int i = 0; i < jsArr.length(); i++) {
			JSONObject jsObject = jsArr.getJSONObject(i);
			String name = jsObject.getString("display_name");
			String eventUrl = jsObject.getString("url");

			Bitmap image = CategoryImage.getFromResourceByName(context, name);
			publishProgress(new Category(image, name, eventUrl));
		    }
		}
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
