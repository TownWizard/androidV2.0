package com.townwizard.android;

import java.util.Arrays;

import com.facebook.Session;
import com.facebook.SessionDefaultAudience;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class FacebookActivity extends FragmentActivity {
    
    protected Session.StatusCallback statusCallback;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    protected Session checkLogin(Bundle savedInstanceState) {
        Session session = Session.getActiveSession();
        if(session != null && session.isOpened()) return session;
        
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
        }
        
        if (session == null) {
            session = new Session(this);
            Session.setActiveSession(session);
        }
        
        if (!session.isOpened()) {
            Session.OpenRequest openRequest = new Session.OpenRequest(this);
            openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
            openRequest.setPermissions(Arrays.asList(new String[]{"friends_status", "publish_stream"}));
            openRequest.setCallback(statusCallback);
            //session.openForRead(openRequest);
            session.openForPublish(openRequest);
        }
        
        return session;
    }    

}
