package com.townwizard.android.facebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.model.GraphLocation;
import com.facebook.model.GraphPlace;
import com.townwizard.android.R;

public class FacebookPlacesAdapter extends BaseAdapter {
    
    private Context context;
    
    private List<GraphPlace> places = new ArrayList<GraphPlace>();
    private Map<String, Integer> checkins = new HashMap<String, Integer>();
    private Map<String, Integer> friendCheckins = new HashMap<String, Integer>();
    
    public FacebookPlacesAdapter(Context context) {
        this.context = context;
    }
    
    public void addPlaces(List<GraphPlace> places) {
        this.places.addAll(places);
        notifyDataSetChanged();
    }
    
    public void addPlaceCheckins(String placeId, Integer count) {
        checkins.put(placeId, count);
        notifyDataSetChanged();
    }
    
    public void addFriendPlaceCheckins(String placeId, Integer count) {
        friendCheckins.put(placeId, count);
        notifyDataSetChanged();
    }
    

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.facebook_place, parent, false);
        }

        GraphPlace place = places.get(position);
        
        TextView nameView = (TextView) view.findViewById(R.id.place_name);
        TextView categoryView = (TextView) view.findViewById(R.id.place_category);
        TextView addressView = (TextView) view.findViewById(R.id.place_address);
        TextView checkinsView = (TextView) view.findViewById(R.id.place_checkins);
        TextView friendCheckinsView = (TextView) view.findViewById(R.id.place_friend_checkins);
        
        nameView.setText(place.getName());
        categoryView.setText(place.getCategory());
        addressView.setText(placeLocationToString(place));
        
        Integer chns = checkins.get(place.getId());
        Integer fChns = friendCheckins.get(place.getId());
        
        checkinsView.setText((chns != null ? chns : 0) + " total");
        friendCheckinsView.setText((fChns != null ? fChns : 0) + " by friends");

        return view;
    }
    
    private String placeLocationToString(GraphPlace place) {
        GraphLocation l = place.getLocation();
        return l.getCity() + ((l.getStreet() != null)  ?  ", " + l.getStreet() : "");
    }

}
