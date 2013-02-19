package com.townwizard.android.facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.townwizard.android.R;


public class FacebookFriendsAdapter extends BaseAdapter {

    private Context context;
    private List<FacebookFriend> allFriends = new ArrayList<FacebookFriend>();
    private List<FacebookFriend> friends = new ArrayList<FacebookFriend>();
    
    public FacebookFriendsAdapter(Context context) {
        this.context = context;
    }
    
    public void addFriends(List<FacebookFriend> friends) {
        Collections.sort(friends, new Comparator<FacebookFriend>() {
            @Override
            public int compare(FacebookFriend lhs, FacebookFriend rhs) {
                String thisName = lhs.getName();
                String thatName = rhs.getName();
                if(thisName != null && thatName != null) {
                    return thisName.compareTo(thatName);
                }
                return -1;
            }
            
        });
        this.allFriends = friends;
        filterFriends(null);
        notifyDataSetChanged();
    }
    
    @SuppressLint("DefaultLocale")
    public void filterFriends(String searchTxt) {
        friends = new ArrayList<FacebookFriend>(allFriends.size());
        for(FacebookFriend f : allFriends) {
            f.setVisible(searchTxt == null || f.getName().toLowerCase().contains(searchTxt.toLowerCase()));
            if(f.isVisible()) {
                friends.add(f);
            }
        }
        notifyDataSetChanged();
    }
    
    public List<FacebookFriend> getSelectedFriends() {
        List<FacebookFriend> l = new LinkedList<FacebookFriend>();
        for(FacebookFriend f : friends) {
            if(f.isSelected()) {
                l.add(f);
            }
        }
        return l;
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

        FacebookFriend friend = friends.get(position);
            
        TextView nameView = (TextView) view.findViewById(R.id.friend_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.friend_image);
            
        nameView.setText(friend.getName());
        imageView.setImageBitmap(friend.getImage());

        ImageView plusButton = (ImageView) view.findViewById(R.id.friend_selected);
        plusButton.setVisibility(friend.isSelected() ? View.VISIBLE : View.INVISIBLE);

        return view;
    }

}
