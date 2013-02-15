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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.townwizard.android.R;
import com.townwizard.android.config.Constants;
import com.townwizard.android.model.FacebookFriend;
import com.townwizard.android.ui.adapter.FacebookFriendsAdapter;
import com.townwizard.android.utils.CurrentLocation;

public class FacebookFriendsList extends ListActivity {
    private FacebookFriendsAdapter mAdapter;
    private ImageButton mImageButtonPost;
    private ImageButton mImageButtonClear;
    private EditText mEditTextSearchFriend;
    private EditText mEditTextUpdStatus;
    private View.OnClickListener mOnClickListener;
    private List<FacebookFriend> mFriendsList = new ArrayList<FacebookFriend>();

    private String mCurrentLocationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.facebook_friends_list);

	Bundle extras = getIntent().getExtras();
	mCurrentLocationID = extras.getString(Constants.FB_PLACE_ID);

	mImageButtonPost = (ImageButton) findViewById(R.id.bt_post);
	mImageButtonClear = (ImageButton) findViewById(R.id.bt_clear_edittext);
	mEditTextSearchFriend = (EditText) findViewById(R.id.et_search_friend);
	mEditTextUpdStatus = (EditText) findViewById(R.id.et_post_status);

	mOnClickListener = new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_clear_edittext: {
		    clearEditText();
		    mAdapter.returnFriendList();
		    mAdapter.notifyDataSetChanged();
		    break;
		}
		case R.id.bt_post: {
		    String message = mEditTextUpdStatus.getText().toString();
		    postCheckin(message);
		}
		}

	    }
	};

	mImageButtonClear.setOnClickListener(mOnClickListener);
	mImageButtonPost.setOnClickListener(mOnClickListener);

	setListAdapter(mAdapter);
	getFriendsList();
    }

    private void clearEditText() {
	mEditTextSearchFriend.setText("");
	mImageButtonClear.setVisibility(View.INVISIBLE);
	hideScreenKeyboard(mEditTextSearchFriend);
    }

    private void postCheckin(String message) {
	Bundle params = new Bundle();
	params.putString("place", mCurrentLocationID);
	if (!message.equals("")) {
	    params.putString("message", message);
	}

	JSONObject coordinates = new JSONObject();
	try {
	    coordinates.put("latitude", CurrentLocation.latitude());
	    coordinates.put("longitude", CurrentLocation.longitude());
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	params.putString("coordinates", coordinates.toString());

	JSONArray frnd_data = new JSONArray();
	for (int i = 0; i < mAdapter.getCount(); i++) {
	    FacebookFriend ff = mAdapter.getItem(i);
	    if (ff.isSelected()) {
		// params.putString("tags", ff.getId())
		frnd_data.put(ff.getId());
	    }
	}
	if (frnd_data.length() > 0) {
	    params.putString("tags", frnd_data.toString());
	}

	String response = null;
	/*
	try {
	    response = FacebookPlaceActivity.sFb.request("me/checkins", params, "POST");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	*/
	Log.d("Checkin resp", response);

	JSONObject js;
	try {
	    js = new JSONObject(response);
	    if (js.has("id")) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("CHECK IN");
		alertDialog.setMessage("You've successfully checked in");
		alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
			finish();
		    }
		});

		alertDialog.show();
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	FacebookFriend ff = mAdapter.getItem(position);
	if (ff.isSelected()) {
	    ff.setSelected(false);
	    mAdapter.notifyDataSetChanged();
	} else {
	    ff.setSelected(true);
	    mAdapter.notifyDataSetChanged();
	}

    }

    private void hideScreenKeyboard(EditText et) {
	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    private void parseFriendResponse(String jsonResponse) {

	try {
	    JSONObject js = new JSONObject(jsonResponse);
	    JSONArray jsArr = js.getJSONArray("data");
	    for (int i = 0; i < jsArr.length(); i++) {
		js = jsArr.getJSONObject(i);
		FacebookFriend item = new FacebookFriend();
		item.setName(js.getString("name"));
		item.setId(js.getString("id"));
		mFriendsList.add(item);

	    }
	    mAdapter = new FacebookFriendsAdapter(this, mFriendsList);
	    setListAdapter(mAdapter);

	    mEditTextSearchFriend.addTextChangedListener(new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		    if (s.length() > 0) {
			mImageButtonClear.setVisibility(View.VISIBLE);
		    } else {
			mImageButtonClear.setVisibility(View.INVISIBLE);
			mAdapter.returnFriendList();
			mAdapter.notifyDataSetChanged();
		    }
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void afterTextChanged(Editable s) {
		    mAdapter.returnFriendList();
		    mAdapter.getFilter().filter(s);
		}
	    });
	} catch (JSONException e) {
	    e.printStackTrace();
	}

    }

    private void getFriendsList() {
	final Handler handler = new Handler();
	/*
	FacebookPlaceActivity.sFacebookRunner.request("me/friends", new RequestListener() {

	    @Override
	    public void onMalformedURLException(MalformedURLException e, Object state) {}

	    @Override
	    public void onIOException(IOException e, Object state) {}

	    @Override
	    public void onFileNotFoundException(FileNotFoundException e, Object state) {}

	    @Override
	    public void onFacebookError(FacebookError e, Object state) {}

	    @Override
	    public void onComplete(String response, Object state) {
		Log.d("Friends", response);
		final String jsonResponse = response;
		handler.post(new Runnable() {
		    @Override
		    public void run() {
			parseFriendResponse(jsonResponse);
		    }

		});
	    }
	});
	*/

    }

}
