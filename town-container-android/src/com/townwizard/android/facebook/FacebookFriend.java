package com.townwizard.android.facebook;

import android.graphics.Bitmap;

import com.facebook.model.GraphUser;

public class FacebookFriend {
    
    private String id;
    private String name; 
    private boolean selected;
    private boolean visible = true;
    private Bitmap image;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
        if(!visible) {
            setSelected(false);
        }
    }
    
    @Override
    public String toString() {
        return id;
    }

    public static FacebookFriend fromGraphUser(GraphUser u) {
        FacebookFriend f = new FacebookFriend();
        f.setId(u.getId());
        f.setName(u.getName());
        return f;
    }
}