package com.townwizard.android.model;

public class Partner {
	
    private final String name;
	private final String url;
	private final String androidAppId;
	private final String imageUrl;
	private final int id;

	public Partner(String name, String url, String androidAppId, int id, String imageUrl) {
		this.name = name;
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

    @Override
    public String toString() {
        return "Partner [name=" + name + ", url=" + url + ", androidAppId=" + androidAppId
                + ", imageUrl=" + imageUrl + ", partnerId=" + id + "]";
    }
}
