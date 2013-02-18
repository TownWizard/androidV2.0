package com.townwizard.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.facebook.FacebookPlace;
import com.townwizard.android.facebook.FacebookPlacesAdapter;
import com.townwizard.android.utils.CurrentLocation;

public class FacebookPlacesActivity extends FacebookActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusCallback = new SessionStatusCallback();        
        setContentView(R.layout.facebook_places);        

        Session session = checkLogin(savedInstanceState);
        if(session.isOpened()) {
            showPlaces();
        }
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
        } else {
            Log.w("Location null", "Location is null.  Can't get places");
        }
        
        ListView listView = (ListView) findViewById(R.id.places_list);
        listView.setAdapter(placesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    final FacebookPlace place = (FacebookPlace) placesAdapter.getItem(position);
                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(FacebookPlacesActivity.this);
                    myAlertDialog.setTitle(Constants.CHECK_IN);
                    myAlertDialog.setMessage(Constants.CHECKIN_CONFIRM + " '" + place.getName() + "'");
                    myAlertDialog.setPositiveButton(Constants.YES,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    startFacebookCheckinActivity(place);
                                }
                            });
                    myAlertDialog.setNegativeButton(Constants.NO, 
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int arg1) {
                                    dialog.cancel();
                                }
                            });
                    myAlertDialog.show();                    
                }
            }
        );
    }
    
    private void startFacebookCheckinActivity(FacebookPlace place) {
        Intent i = new Intent();
        i.setClass(this, FacebookCheckinActivity.class);
        i.putExtra(Constants.FB_PLACE_ID, place.getId());
        startActivity(i);
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if(session.isOpened()) {
                showPlaces();
            }
        }
    }
    
    private Request getPlacesSearchRequest(
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
                    for(GraphObject o : data) {
                       JSONObject json = o.getInnerJSONObject();
                       places.add(placeFromJson(json));
                    }
                }
            }
            facebookPlacesAdapter.addPlaces(places);
            
            String commaSeparatedPlaceIds = collectPlaceIds(places);
            String fql =                     
                "SELECT target_id FROM checkin " +  
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
        
        private FacebookPlace placeFromJson(JSONObject json) {
            FacebookPlace place = new FacebookPlace();
            place.setId(json.optString("id"));
            place.setName(json.optString("name"));
            place.setCategory(json.optString("category"));
            String checkins = json.optString("checkins");
            if(checkins.length() == 0) checkins = "0";
            place.setCheckins(checkins);
            JSONObject locJson = json.optJSONObject("location");
            if(locJson != null) {
                place.setCity(locJson.optString("city"));
                place.setStreet(locJson.optString("street"));
            }
            JSONObject pic = json.optJSONObject("picture");
            if(pic != null) {
                JSONObject picData = pic.optJSONObject("data");
                if(picData != null) {
                    place.setImageUrl(picData.optString("url"));                    
                }                
            }
            return place;
        }

        private class FriendCheckinsRequestCallback implements Request.Callback {

            @Override
            public void onCompleted(Response response) {
                GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
                if (multiResult != null) {
                    GraphObjectList<GraphObject> data = multiResult.getData();
                    if (data != null) {
                        Map<String, Integer> friendCheckins = new HashMap<String, Integer>();
                        for(GraphObject o : data) {
                           JSONObject json = o.getInnerJSONObject();
                           String placeId = json.optString("target_id");
                           Integer count = friendCheckins.get(placeId);
                           if(count == null) count = 1;
                           else count = count + 1;
                           friendCheckins.put(placeId, count);  
                        }
                        facebookPlacesAdapter.addFriendCheckins(friendCheckins);
                    }
                }
            }
        }
    }
}



























