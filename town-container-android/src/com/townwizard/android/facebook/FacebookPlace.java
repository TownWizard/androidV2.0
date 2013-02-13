package com.townwizard.android.facebook;

import android.graphics.Bitmap;

public class FacebookPlace {
	
    private String name;
	private String category;
	private String street;
	private String id;
	private double latitude;
	private double longitude;
	private Bitmap image;
	private String imageUrl;
	private String allCheckins;
	private String friendsCheckins;
	
	public String getAllCheckins() {
		return allCheckins;
	}

	public void setAllCheckins(String allCheckins) {
		this.allCheckins = allCheckins;
	}

	public String getFriendsCheckins() {
		return friendsCheckins;
	}

	public void setFriendsCheckins(String friendsCheckins) {
		this.friendsCheckins = friendsCheckins;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Bitmap getImage(){
		return this.image;
	}
	public void setImage(Bitmap image){
		this.image = image;
	}
}
