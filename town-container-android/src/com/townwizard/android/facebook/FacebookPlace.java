package com.townwizard.android.facebook;

public class FacebookPlace {

    private String id;
    private String name;
	private String category;
	private String city;
	private String street;
	private String imageUrl;
	private String checkins;
    
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
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getCheckins() {
        return checkins;
    }
    public void setCheckins(String checkins) {
        this.checkins = checkins;
    }
}
