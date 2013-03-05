package com.townwizard.android;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;

public abstract class FacebookActivity extends Activity {
    
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
        
        if (session == null) {            
            session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
        }
        
        if(session != null && (session.isClosed() || session.getPermissions().isEmpty())) {
            session.closeAndClearTokenInformation();
            session = null;
        }

        if (session == null) {
            session = new Session(this);
            Session.setActiveSession(session);
        }
        
        if (!session.isOpened()) {
            Session.OpenRequest openRequest = new Session.OpenRequest(this);
            openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            openRequest.setCallback(statusCallback);
            openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
            openRequest.setPermissions(Arrays.asList(new String[]{"publish_stream"}));
            session.openForPublish(openRequest);
        }
        
        return session;
    }
    
    protected void clearSession() {
        Session session = Session.getActiveSession();
        if(session != null) {
            session.closeAndClearTokenInformation();
        }
    }

}
