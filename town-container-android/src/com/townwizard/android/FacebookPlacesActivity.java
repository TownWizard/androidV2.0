package com.townwizard.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.townwizard.android.config.Config;
import com.townwizard.android.facebook.FacebookPlace;
import com.townwizard.android.facebook.FacebookPlacesAdapter;
import com.townwizard.android.utils.CurrentLocation;

public class FacebookPlacesActivity extends FragmentActivity {

    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_places);

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
        
        if (!session.isOpened()) {
            Session.OpenRequest openRequest = new Session.OpenRequest(this);
            openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
            openRequest.setPermissions(Arrays.asList(new String[]{"friends_status"}));
            openRequest.setCallback(statusCallback);
            session.openForRead(openRequest);
        }
        
        return session;
    }
        
    private void showPlaces() {
        final FacebookPlacesAdapter placesAdapter = new FacebookPlacesAdapter(this);
        Location location = CurrentLocation.location();
        
        if(location != null) {
            getPlacesSearchRequest(
                    Session.getActiveSession(), location,
                    Config.FB_CHECKIN_DISTANCE_METERS, 
                    Config.FB_CHECKIN_RESULTS_LIMIT, 
                    null/*searchText*/,
                    new PlacesRequestCallback(placesAdapter)).executeAsync();
        }
        
        ListView listView = (ListView) findViewById(R.id.places_list);
        listView.setAdapter(placesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    FacebookPlace place = (FacebookPlace) placesAdapter.getItem(position);
                    /***********  TODO: Delete this ***********************/
                    Session.getActiveSession().closeAndClearTokenInformation();
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
                showPlaces();
            }
        }
    }
    
    static Request getPlacesSearchRequest(
            Session session, Location location, int radiusInMeters,
            int resultsLimit, String searchText, Callback callback) {
            
        Request request = Request.newPlacesSearchRequest(session, location, radiusInMeters,
                resultsLimit, searchText, null);
            
        request.getParameters().putString("fields", "name,category,location,picture,checkins");
        request.setCallback(callback);
        return request;            
    }
    
    private class PlacesRequestCallback implements Request.Callback {
        
        private FacebookPlacesAdapter facebookPlacesAdapter;
        
        PlacesRequestCallback(FacebookPlacesAdapter facebookPlacesAdapter) {
            this.facebookPlacesAdapter = facebookPlacesAdapter;
        }
        
        @Override
        public void onCompleted(Response response) {
            List<FacebookPlace> places = new ArrayList<FacebookPlace>();
            GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
            if (multiResult != null) {
                GraphObjectList<GraphObject> data = multiResult.getData();
                if (data != null) {
                    try {
                        for(GraphObject o : data) {
                           JSONObject json = o.getInnerJSONObject();
                           places.add(placeFromJson(json));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException (e);
                    }
                }
            }
            facebookPlacesAdapter.addPlaces(places);
            
            String commaSeparatedPlaceIds = collectPlaceIds(places);
            String fql =                     
                "SELECT author_uid, target_id FROM checkin " +  
                "WHERE author_uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND target_id IN (" +
                            commaSeparatedPlaceIds + ")";
            Bundle parameters = new Bundle(1);
            parameters.putString("q", fql);
            new Request(Session.getActiveSession(), "fql", parameters, null, 
                    new FriendCheckinsRequestCallback()).executeAsync();
        } 
        private String collectPlaceIds(List<FacebookPlace> places) {
            StringBuilder sb = new StringBuilder();
            Iterator<FacebookPlace> iter = places.iterator();
            while(iter.hasNext()) {
                FacebookPlace p = iter.next();
                sb.append("'").append(p.getId()).append("'");
                if(iter.hasNext()) sb.append(",");
            }            
            return sb.toString();
        }
        
        private FacebookPlace placeFromJson(JSONObject json) throws JSONException {
            FacebookPlace place = new FacebookPlace();
            place.setId(json.optString("id"));
            place.setName(json.optString("name"));
            place.setCategory(json.optString("category"));
            place.setCheckins(json.has("checkins") ? json.getString("checkins") : "0");
            JSONObject locJson = json.optJSONObject("location");
            if(locJson != null) {
                place.setCity(locJson.optString("city"));
                place.setStreet(locJson.optString("street"));
            }
            JSONObject pic = json.optJSONObject("picture");
            if(pic != null) {
                JSONObject picData = pic.optJSONObject("data");
                if(picData != null) {
                    place.setImageUrl(picData.getString("url"));                    
                }                
            }
            return place;
        }

        private class FriendCheckinsRequestCallback implements Request.Callback {

            @Override
            public void onCompleted(Response response) {
                System.out.println(response);
                
            }
            
            
            
        }

    
    }
    

}



























