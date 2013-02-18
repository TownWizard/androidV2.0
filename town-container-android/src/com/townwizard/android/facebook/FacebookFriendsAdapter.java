package com.townwizard.android.facebook;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.townwizard.android.R;

public class FacebookFriendsAdapter extends BaseAdapter {

    private Context context;
    private List<GraphUser> friends = new ArrayList<GraphUser>();
    
    public FacebookFriendsAdapter(Context context) {
        this.context = context;
    }
    
    public void addFriends(List<GraphUser> friends) {
        Collections.sort(friends, new Comparator<GraphUser>() {
            @Override
            public int compare(GraphUser lhs, GraphUser rhs) {
                String thisName = lhs.getName();
                String thatName = rhs.getName();
                if(thisName != null && thatName != null) {
                    return thisName.compareTo(thatName);
                }
                return -1;
            }
            
        });
        this.friends = friends;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
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
                view = inflater.inflate(R.layout.friend, parent, false);
        }
        
        GraphUser friend = friends.get(position);
        
        TextView nameView = (TextView) view.findViewById(R.id.friend_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.friend_image);
        
        nameView.setText(friend.getName());
        
        String imageUrl = "http://graph.facebook.com/"+friend.getId()+"/picture";        
        try {
            InputStream in = new URL(imageUrl).openStream();
            imageView.setImageBitmap(BitmapFactory.decodeStream(in));
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        return view;
    }

}
