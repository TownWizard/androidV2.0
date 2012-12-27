package com.townwizard.android.model;

import android.graphics.Bitmap;

public class FacebookFriend {
    private String name;
    private Bitmap pictures;
    private String id;
    private boolean selected = false;

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

    public Bitmap getPictures() {
	return pictures;
    }

    public void setPictures(Bitmap pictures) {
	this.pictures = pictures;
    }

    public boolean isSelected() {
	return selected;
    }

    public void setSelected(boolean selected) {
	this.selected = selected;
    }

    public FacebookFriend() {

    }

}
