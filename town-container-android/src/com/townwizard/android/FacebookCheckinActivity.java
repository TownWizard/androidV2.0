package com.townwizard.android;

import java.util.List;

import android.os.Bundle;
import android.view.View;
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
import com.townwizard.android.facebook.FacebookFriendsAdapter;

public class FacebookCheckinActivity extends FacebookActivity {

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
        
        
        
        Session session = checkLogin(savedInstanceState);
        if(session.isOpened()) {
            showFriends();
        }
    }
    
    
    private void postCheckin(String message) {
        System.out.println(message);
    }
    
    private void showFriends() {
        final FacebookFriendsAdapter friendsAdapter = new FacebookFriendsAdapter(this);
        Request.executeMyFriendsRequestAsync(Session.getActiveSession(), 
                new FriendListCallback(friendsAdapter));
        
        ListView listView = (ListView) findViewById(R.id.friend_list);
        listView.setAdapter(friendsAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    final GraphUser friend = (GraphUser) friendsAdapter.getItem(position);                    
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
        public void onCompleted(List<GraphUser> friends, Response response) {
            friendsAdapter.addFriends(friends);            
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
