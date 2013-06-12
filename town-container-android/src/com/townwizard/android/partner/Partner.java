package com.townwizard.android.partner;

import com.townwizard.android.config.Constants;

public class Partner implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
	private final String url;
	private final String androidAppId;
	private final String imageUrl;
	private final int id;

	public Partner(String name, String url, String androidAppId, int id, String imageUrl) {
		this.name = handleSpecialCharacters(name);
		this.url = url;
		this.androidAppId = androidAppId;
		this.id = id;
		this.imageUrl = imageUrl;
	}
	
	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getAndroidAppId() {
		return androidAppId;
	}
	
	public String getImageUrl(){
		return imageUrl;
	}

	public int getId(){
		return id;
	}
	
	public boolean isContentPartner() {
	    return Constants.CONTENT_PARTNER_EVENTS.equals(name) ||
	           Constants.CONTENT_PARTNER_RESTAURANTS.equals(name) ||
	           Constants.CONTENT_PARTNER_PLACES.equals(name);
	}

    @Override
    public String toString() {
        return "Partner [name=" + name + ", url=" + url + ", androidAppId=" + androidAppId
                + ", imageUrl=" + imageUrl + ", partnerId=" + id + "]";
    }
    
    private String handleSpecialCharacters (String s) {
        return s.replace("\\\'", "'");
    }
}
