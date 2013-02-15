package com.townwizard.android.facebook;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.townwizard.android.R;

public class FacebookPlacesAdapter extends BaseAdapter {
    
    private Context context;
    
    private List<FacebookPlace> places = new ArrayList<FacebookPlace>();
    
    private Map<String, Integer> friendCheckins = new HashMap<String, Integer>();
    
    public FacebookPlacesAdapter(Context context) {
        this.context = context;
    }
    
    public void addPlaces(List<FacebookPlace> places) {
        this.places.addAll(places);
        notifyDataSetChanged();
    }

    public void addFriendCheckins(Map<String, Integer> friendCheckins) {
        this.friendCheckins = friendCheckins;
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

        FacebookPlace place = places.get(position);
        
        TextView nameView = (TextView) view.findViewById(R.id.place_name);
        TextView categoryView = (TextView) view.findViewById(R.id.place_category);
        TextView addressView = (TextView) view.findViewById(R.id.place_address);
        TextView checkinsView = (TextView) view.findViewById(R.id.place_checkins);
        TextView friendCheckinsView = (TextView) view.findViewById(R.id.place_friend_checkins);
        ImageView imageView = (ImageView) view.findViewById(R.id.place_image);
        
        nameView.setText(place.getName());
        categoryView.setText(place.getCategory());
        addressView.setText(placeLocationToString(place));
        try {
            InputStream in = new URL(place.getImageUrl()).openStream();
            imageView.setImageBitmap(BitmapFactory.decodeStream(in));
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        String chns = place.getCheckins();
        Integer fChns = friendCheckins.get(place.getId());
        
        checkinsView.setText((chns != null ? chns : 0) + " total");
        friendCheckinsView.setText((fChns != null ? fChns : 0) + " by friends");

        return view;
    }
    
    private String placeLocationToString(FacebookPlace place) {        
        return place.getCity() + ((place.getStreet() != null)  ?  ", " + place.getStreet() : "");
    }

}
