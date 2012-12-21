package com.mlsdev.android.townwizard.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.mlsdev.andoid.townwizard.ui.adapter.CategoriesAdapter;
import com.mlsdev.android.townwizard.model.Categories;

public class SearchCategories extends AsyncTask<String, Categories, Void> {

    private static final String DEFAULT_URL = "http://www.townwizardcontainerapp.com/";
    private static final String PARTNER_API_URL = "api/section/partner/";
    private CategoriesAdapter mCategoriesAdapter;
    private int mStatus;

    public SearchCategories(CategoriesAdapter categoriesAdapter) {
	mCategoriesAdapter = categoriesAdapter;
    }

    @Override
    protected void onProgressUpdate(Categories... values) {
	// TODO Auto-generated method stub
	super.onProgressUpdate(values);
	mCategoriesAdapter.addItem(values[0]);
	mCategoriesAdapter.notifyDataSetChanged();
    }

    @Override
    protected Void doInBackground(String... params) {
	String mId = params[0];
	URL url = null;
	try {
	    url = new URL(DEFAULT_URL + PARTNER_API_URL + URLEncoder.encode(mId, "UTF-8"));
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    String response = ServerConnector.getServerResponse(url);
	    Log.d("JSON = ", response);

	    if (response.length() > 0) {

		JSONObject mMainJsonObject = new JSONObject(response);
		mStatus = mMainJsonObject.getInt("status");

		if (mStatus == 1) {
		    JSONArray jsArr = mMainJsonObject.getJSONArray("data");
		    for (int i = 0; i < jsArr.length(); i++) {
			JSONObject jsObject = jsArr.getJSONObject(i);
			String name = jsObject.getString("display_name");
			String image_url = jsObject.getString("image_url");
			String eventUrl = jsObject.getString("url");

			URL imageUrl = new URL(DEFAULT_URL + image_url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			InputStream is = conn.getInputStream();
			Bitmap image = BitmapFactory.decodeStream(is);
			is.close();
			conn.disconnect();
			publishProgress(new Categories(image, name, eventUrl));
		    }
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
