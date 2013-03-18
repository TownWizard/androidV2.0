package com.townwizard.android;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.townwizard.android.config.Constants;
import com.townwizard.android.facebook.FacebookFriend;
import com.townwizard.android.facebook.FacebookFriendsAdapter;
import com.townwizard.android.utils.Utils;

public class FacebookCheckinActivity extends FacebookActivity {
    
    private FacebookFriendsAdapter friendsAdapter;
    private static Handler handler;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusCallback = new SessionStatusCallback();
        setContentView(R.layout.facebook_checkin);
        
        Header.build(this);
        
        Utils.checkConnectivity(this);

        ImageButton postButton = (ImageButton) findViewById(R.id.facebook_status_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                String placeId = extras.getString(Constants.FB_PLACE_ID);
                EditText statusEditText = (EditText) findViewById(R.id.facebook_status);
                String message = statusEditText.getText().toString();                
                postCheckin(placeId, message, friendsAdapter.getSelectedFriends());
            }
        });
        
        EditText searchFriendsEditText = (EditText) findViewById(R.id.search_friends);
        searchFriendsEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && friendsAdapter != null) {
                    Utils.hideScreenKeyboard(v, FacebookCheckinActivity.this);
                    filterFriends(v);
                }
            }
            
        });
        
        Session session = checkLogin(savedInstanceState);
        if(session.isOpened()) {
            showFriends();
        }
        
        handler = new FacebookCheckingHandler(friendsAdapter);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View searchFriendsView = findViewById(R.id.search_friends);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive(searchFriendsView)) {
            Utils.hideScreenKeyboard(searchFriendsView, FacebookCheckinActivity.this);
            filterFriends(searchFriendsView);
            searchFriendsView.clearFocus();
        }
        return true;
    }

    private void filterFriends(View input) {
        EditText view = (EditText)input;
        String searchTxt = view.getText().toString();
        searchTxt = searchTxt.length() > 0 ? searchTxt : null;
        friendsAdapter.filterFriends(searchTxt);        
    }
    
    private void postCheckin(String placeId, String msg, List<FacebookFriend> taggedFriends) {
        final ProgressDialog dialog = ProgressDialog.show(
                this, null, getResources().getString(R.string.checkin_wait), true);
        
        String tags = (taggedFriends != null && !taggedFriends.isEmpty()) ? Utils.join(taggedFriends) : "";

        new AsyncTask<String, Void, Response>() {            
            @Override
            protected Response doInBackground(String... prms) {
                String placeId = prms[0];
                String msg = prms[1];
                String tags = prms[2];                
                String message = (msg != null) ? msg.trim() : "";                
                GraphObject params = GraphObject.Factory.create();
                params.setProperty("place", placeId);
                params.setProperty("message", message);
                params.setProperty("tags", tags);
                Request request = Request.newPostRequest(Session.getActiveSession(), "me/feed", params, null);
                Response response = Request.executeAndWait(request);
                return response;
            }
            
            @Override
            protected void onPostExecute(Response response) {        
                dialog.dismiss();
                boolean success = (response.getError() == null && 
                        response.getGraphObject().getInnerJSONObject().has("id"));
                
                if(!success) {                    
                    Log.w("Checkin failure", response.toString());
                }

                Resources res = getResources();
                String message = null;
                boolean navigateAway = false;
                if(success) {
                    message = res.getString(R.string.checkin_success);
                    navigateAway = true;
                } else {
                    FacebookRequestError error = response.getError();
                    if(error != null) {
                        String errorMessage = error.getErrorMessage();
                        if(errorMessage != null) {
                            message = "Facebook error:\n" + errorMessage;
                        } else {
                            message = res.getString(R.string.checkin_failure); 
                        }
                    }
                    if("OAuthException".equals(response.getError().getErrorType())) {
                        clearSession();
                        navigateAway = true;
                    }
                }

                showAlert(message, navigateAway);
            }
            
            private void showAlert(final String message, final boolean navigateAway) {
                Resources res = getResources();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FacebookCheckinActivity.this);
                alertDialog.setTitle(res.getString(R.string.check_in));
                alertDialog.setMessage(message);
                alertDialog.setPositiveButton(res.getString(R.string.cont),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if(navigateAway) {
                                    goBackToPlaces();
                                }
                            }
                        });
                alertDialog.show();
            }
            
            private void goBackToPlaces() {
                Intent web = new Intent(FacebookCheckinActivity.this, WebActivity.class);
                web.putExtra(Constants.OVERRIDE_TRANSITION, true);
                startActivity(web);
            }            
        }.execute(placeId, msg, tags);
    }    
    
    private void showFriends() {
        friendsAdapter = new FacebookFriendsAdapter(this);
        Request.executeMyFriendsRequestAsync(Session.getActiveSession(), 
                new FriendListCallback(friendsAdapter));
        
        ListView listView = (ListView) findViewById(R.id.friend_list);
        listView.setAdapter(friendsAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    FacebookFriend friend = (FacebookFriend) friendsAdapter.getItem(position);
                    friend.setSelected(!friend.isSelected());
                    friendsAdapter.notifyDataSetChanged();
                }
            }
        );
    }
    
    private class FriendListCallback implements Request.GraphUserListCallback {        
        
        private FacebookFriendsAdapter friendsAdapter;
        
        FriendListCallback(FacebookFriendsAdapter friendsAdapter) {
            this.friendsAdapter = friendsAdapter;
        }

        @Override
        public void onCompleted(List<GraphUser> users, Response response) {
            if(users != null) {
                List<FacebookFriend> friends = new ArrayList<FacebookFriend>(users.size());
                for(GraphUser u : users) {
                    friends.add(FacebookFriend.fromGraphUser(u));
                }
                
                friendsAdapter.addFriends(friends);                
                
                ExecutorService imageDownloaders = Executors.newFixedThreadPool(20);
                
                for(FacebookFriend f : friendsAdapter.getAllFriends()) {
                    imageDownloaders.submit(new FriendImageDownloader(f));
                }                
            }
        }
    }  
    
    private class FriendImageDownloader implements Runnable {

        private FacebookFriend friend;

        private FriendImageDownloader(FacebookFriend friend) {
            this.friend = friend;
        }

        @Override
        public void run() {
            Bitmap image = downloadBitmap("http://graph.facebook.com/" + friend.getId() + "/picture");
            if (image != null) {
                friend.setImage(image);
                handler.sendEmptyMessage(0);
            }
        }

        private Bitmap downloadBitmap(String urlStr) {
            InputStream in = null;
            try {
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                conn.setUseCaches(true);
                in = conn.getInputStream();
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Image download", e.getMessage());
            } finally {
                try { if (in != null) in.close(); } catch (Exception e) { e.printStackTrace(); }
            }
            return null;
        }
    }    
    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if(session.isOpened()) {
                showFriends();
            }
        }
    }
    
    private static class FacebookCheckingHandler extends Handler {
        
        private FacebookFriendsAdapter friendsAdapter;
        
        private FacebookCheckingHandler(FacebookFriendsAdapter friendsAdapter) {
            this.friendsAdapter = friendsAdapter;
        }

        @Override
        public void handleMessage(Message m) {
            if(friendsAdapter != null) {
                friendsAdapter.notifyDataSetChanged();
            }
        }        
    }    
}
