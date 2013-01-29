package com.townwizard.android.partner;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.townwizard.android.R;
import com.townwizard.android.utils.ServerConnector;
import com.townwizard.android.utils.TownWizardConstants;

public class SearchPartners extends AsyncTask<String, Partner, Integer> {
    
    private static final String OFFSET = "&offset=";
    private URL mUrl;
    private int mStatus = -1;
    private PartnersAdapter mListAdapter;
    private Context mContext;

    public SearchPartners(Context context, PartnersAdapter listAdapter) {
	mListAdapter = listAdapter;
	mContext = context;
    }

    @Override
    protected void onProgressUpdate(Partner... values) {
	super.onProgressUpdate(values);
	mListAdapter.addItem(values[0]);
    }

    @Override
    protected Integer doInBackground(String... params) {
	String searchRequest = URLEncoder.encode(params[0]);
	int offset = Integer.parseInt(params[1]);
	try {
	    mUrl = new URL(TownWizardConstants.PARTNER_API + "?" + searchRequest + OFFSET + offset);
	    Log.d("Search URL = ", mUrl.toString());
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
	try {
	    String response = ServerConnector.getServerResponse(mUrl);
	    Log.d("JSON = ", response);

	    JSONObject mMainJsonObject = new JSONObject(response);
	    mStatus = mMainJsonObject.getInt("status");

	    if (mStatus == 1) {
		Log.d("mOffset", Integer.toString(offset));

		JSONArray jsArr = mMainJsonObject.getJSONArray("data");
		for (int i = 0; i < jsArr.length(); i++) {
		    JSONObject jsObject = jsArr.getJSONObject(i);
		    // Log.d("JSONd in array = ", jsObject.toString());
		    int partnerId = jsObject.getInt("id");
		    String name = jsObject.getString("name");
		    String url = jsObject.getString("website_url");
		    String androidAppId = jsObject.getString("android_app_id");
		    String imageUrl = jsObject.getString("image");
		    Log.d("partner_id", Integer.toString(partnerId));
		    Log.d("name", name);
		    Log.d("url", url);

		    if (url.charAt(url.length() - 1) != '/') {
			url += "/";
		    }
		    Log.d("app_id", androidAppId);

		    publishProgress(new Partner(name, url, androidAppId, partnerId, imageUrl));
		}
		JSONObject metaInf = mMainJsonObject.getJSONObject("meta");
		int total = metaInf.getInt("total");
		int limit = metaInf.getInt("limit");
		int nextOffcet = 0;
		if (offset + limit < total) {
		    nextOffcet = metaInf.getInt("next_offset");
		    offset = nextOffcet;
		    publishProgress(new Partner("Load more", "", "", -1, ""));
		} else {
		    offset = 0;
		}

	    } else {
		mStatus = 0;
	    }

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return offset;
    }

    @Override
    protected void onPostExecute(Integer result) {
	super.onPostExecute(result);
	if (mStatus == 0) {
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	    String title = mContext.getResources().getString(R.string.whoops);
	    String message = mContext.getResources().getString(R.string.partners_not_found);
	    alertDialog.setTitle(title);
	    alertDialog.setMessage(message);
	    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    dialog.cancel();
		}
	    });

	    alertDialog.show();
	}
    }

}
