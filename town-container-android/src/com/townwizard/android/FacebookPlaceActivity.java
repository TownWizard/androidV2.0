package com.townwizard.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.townwizard.android.R;
import com.townwizard.android.config.Constants;
import com.townwizard.android.facebook.FacebookPlace;
import com.townwizard.android.ui.adapter.FacebookPlacesAdapter;
import com.townwizard.android.utils.CurrentLocation;

public class FacebookPlaceActivity extends ListActivity {
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPrefs;
    //private ImageView mImageView;
    private TextView mTextView;

    public static Facebook sFb = new Facebook("374159485950604");
    public static AsyncFacebookRunner sFacebookRunner = new AsyncFacebookRunner(sFb);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_places);
        Bundle extras = getIntent().getExtras();
//        Bitmap bitmap = extras.getParcelable(TownWizardConstants.HEADER_IMAGE);
//        mImageView = (ImageView) findViewById(R.id.iv_header_fb_places);
//        mImageView.setImageBitmap(bitmap);
        //mTextView = (TextView) findViewById(R.id.tv_header_fb_places);
        mTextView = null;
        mTextView.setText(extras.getString(Constants.CATEGORY_NAME));

        mPrefs = getPreferences(MODE_PRIVATE);
        mProgressDialog = new ProgressDialog(FacebookPlaceActivity.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.hide();
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if (access_token != null) {
            sFb.setAccessToken(access_token);
        }
        if (expires != 0) {
            sFb.setAccessExpires(expires);
        }

        /*
         * Only call authorize if the access_token has expired.
         */
        if (!sFb.isSessionValid()) {
            authorizeFacebook();
        }
    }

    private void authorizeFacebook() {
        sFb.authorize(this, new String[] { "publish_stream",
                "publish_checkins", "friends_checkins", "user_checkins" },
                new DialogListener() {
                    @Override
                    public void onComplete(Bundle values) {
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("access_token", sFb.getAccessToken());
                        Log.d("access token", sFb.getAccessToken());
                        editor.putLong("access_expires", sFb.getAccessExpires());
                        editor.commit();
                    }

                    @Override
                    public void onFacebookError(FacebookError error) {
                    }

                    @Override
                    public void onError(DialogError e) {
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        sFb.extendAccessTokenIfNeeded(this, null);
        getFacebookPlaces();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sFb.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final FacebookPlace item = (FacebookPlace) getListAdapter().getItem(position);

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("CHECK IN");
        myAlertDialog.setMessage("Do you want to check in to " + "'" + item.getName() + "'");
        myAlertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent friendsList = new Intent();
                        friendsList.setClass(FacebookPlaceActivity.this, FacebookFriendsList.class);
                        friendsList.putExtra(Constants.ITEM_LOCATION, item.getId());
                        startActivity(friendsList);
                    }
                });
        myAlertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
        myAlertDialog.show();
    }

    public void parseFacebookResponse(String response) {
        List<FacebookPlace> places = new ArrayList<FacebookPlace>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("error")) {
                authorizeFacebook();
            } else {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    final FacebookPlace item = new FacebookPlace();
                    JSONObject js = jsonArray.getJSONObject(i);
                    String name = js.getString("name");
                    String category = js.getString("category");
                    String id = js.getString("id");
                    // String urlImage = js.getString("picture");
                    js = js.getJSONObject("location");
                    double latitude = js.getDouble("latitude");
                    double longitude = js.getDouble("longitude");
                    String street;

                    if (js.has("street")) {
                        street = js.getString("street");
                    } else
                        street = "";

                    item.setCategory(category);
                    //item.setLatitude(latitude);
                    //item.setLongitude(longitude);
                    item.setStreet(street);
                    item.setName(name);
                    item.setId(id);
                    places.add(item);
                }
            }

            // Log.d(TAG, Integer.toString(places.size()));
            setListAdapter(new FacebookPlacesAdapter(this, places));
            mProgressDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getFacebookPlaces() {
        final Handler handler = new Handler();

        Bundle params = new Bundle();
        JSONObject location = new JSONObject();
        try {
            location.put("latitude", CurrentLocation.latitude());
            location.put("longitude", CurrentLocation.longitude());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        params.putString("type", "place");
        try {
            params.putString("center", location.getString("latitude") + "," + location.getString("longitude"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        params.putString("distance", "2000");

        mProgressDialog.show();

        sFacebookRunner.request("search", params, new RequestListener() {
            @Override
            public void onMalformedURLException(MalformedURLException e,
                    Object state) {
            }

            @Override
            public void onIOException(IOException e, Object state) {

            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e,
                    Object state) {
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
                Log.d("Error", e.getMessage());

            }

            @Override
            public void onComplete(String response, Object state) {
                Log.d("Place=", response);
                final String jsonResponse = response;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        parseFacebookResponse(jsonResponse);
                    }
                });
            }
        });
    }
}
