package com.townwizard.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphPlace;
import com.townwizard.android.config.Config;
import com.townwizard.android.facebook.FacebookPlacesAdapter;
import com.townwizard.android.utils.CurrentLocation;

public class FacebookPlacesActivity extends FragmentActivity {

    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_places);
        
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        
        Session session = checkLogin(savedInstanceState);
        if(session.isOpened()) {
            showPlaces();
        }
    }
    
    
    private Session checkLogin(Bundle savedInstanceState) {
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

        /*
        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        }
        */
        
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            session = Session.openActiveSession(this, true, statusCallback);
        }
        
        return session;
    }
        
    private void showPlaces() {
        final FacebookPlacesAdapter placesAdapter = new FacebookPlacesAdapter(this);
        Location location = CurrentLocation.location();
        
        if(location != null) {
            RequestAsyncTask task = Request.executePlacesSearchRequestAsync(
                    Session.getActiveSession(), location,
                    Config.FB_CHECKIN_DISTANCE_METERS, 
                    Config.FB_CHECKIN_RESULTS_LIMIT, 
                    null/*searchText*/,
                    new PlacesRequestCallback(placesAdapter));
            
            try {
                List<Response> responses = task.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        ListView listView = (ListView) findViewById(R.id.places_list);
        listView.setAdapter(placesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    GraphPlace place = (GraphPlace) placesAdapter.getItem(position);
                    startCheckinActivity(place);
                }
            }
        );
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if(session.isOpened()) {
                //showPlaces();
            }
        }
    }
    
    private class PlacesRequestCallback implements Request.GraphPlaceListCallback {
        
        private FacebookPlacesAdapter facebookPlacesAdapter;
        
        PlacesRequestCallback(FacebookPlacesAdapter facebookPlacesAdapter) {
            this.facebookPlacesAdapter = facebookPlacesAdapter;
        }
        
        @Override
        public void onCompleted(List<GraphPlace> places, Response response) {
            if(places != null) {
                facebookPlacesAdapter.addPlaces(places);            
                List<String> placeIds = collectPlaceIds(places);
            }
        } 
        
        private List<String> collectPlaceIds(List<GraphPlace> places) {
            List<String> ids = new ArrayList<String>(places.size());
            for(GraphPlace p : places) {
                ids.add(p.getId());
            }
            return ids;
        }
    }
    
    private void startCheckinActivity(GraphPlace place) {
        //TODO: imlement
    }
}



























