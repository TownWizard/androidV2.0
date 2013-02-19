package com.townwizard.android;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.townwizard.android.config.Constants;
import com.townwizard.android.facebook.FacebookFriend;
import com.townwizard.android.facebook.FacebookFriendsAdapter;
import com.townwizard.android.utils.BitmapDownloaderTask;

public class FacebookCheckinActivity extends FacebookActivity {
    
    private FacebookFriendsAdapter friendsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusCallback = new SessionStatusCallback();
        setContentView(R.layout.facebook_checkin);
        
    
        Bundle extras = getIntent().getExtras();
        String placeId = extras.getString(Constants.FB_PLACE_ID);

        final EditText statusEditText = (EditText) findViewById(R.id.facebook_status);
        
        ImageButton postButton = (ImageButton) findViewById(R.id.facebook_status_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = statusEditText.getText().toString();
                postCheckin(message);
            }
        });
        
        EditText searchFriendsEditText = (EditText) findViewById(R.id.search_friends);
        searchFriendsEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText view = (EditText)v;
                if(Constants.SEARCH_FRIENDS.equals(view.getText().toString())) {
                   view.getText().clear();
                }
                return false;
            }            
        });
        searchFriendsEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && friendsAdapter != null) {
                    EditText view = (EditText)v;
                    String searchTxt = view.getText().toString();
                    searchTxt = searchTxt.length() > 0 ? searchTxt : null;
                    friendsAdapter.filterFriends(searchTxt);
                }
            }
            
        });
        
        
        
        
        
        Session session = checkLogin(savedInstanceState);
        if(session.isOpened()) {
            showFriends();
        }
    }
    
    
    private void postCheckin(String message) {
        System.out.println(message);
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
    
    private static class FriendListCallback implements Request.GraphUserListCallback {        
        
        private FacebookFriendsAdapter friendsAdapter;
        
        FriendListCallback(FacebookFriendsAdapter friendsAdapter) {
            this.friendsAdapter = friendsAdapter;
        }

        @Override
        public void onCompleted(List<GraphUser> users, Response response) {
            List<FacebookFriend> friends = new ArrayList<FacebookFriend>(users.size());
            for(GraphUser u : users) {
                friends.add(FacebookFriend.fromGraphUser(u));
            }
            
            friendsAdapter.addFriends(friends);
            
            for(final FacebookFriend f : friends) {
                new BitmapDownloaderTask() {
                    @Override
                    protected void onPostExecute(Bitmap result) {
                        f.setImage(result);
                        friendsAdapter.notifyDataSetChanged();
                    }
                }.execute("http://graph.facebook.com/"+f.getId()+"/picture");
            }
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
}
