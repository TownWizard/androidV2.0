package com.townwizard.android.model;

public class Partner {
	private String mName;
	private String mUrl;
	private String mAndroidAppId;
	private String mImageUrl;
	private int mPartnerId;

	public Partner(String name, String url, String androidAppId, int partnerId, String imageUrl) {
		mName = name;
		mUrl = url;
		mAndroidAppId = androidAppId;
		mPartnerId = partnerId;
		mImageUrl = imageUrl;
	}
	
	
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public String getAndroidAppId() {
		return mAndroidAppId;
	}
	public String getImageUrl(){
		return mImageUrl;
	}

	public void setAndroidAppId(String androidAppId) {
		this.mAndroidAppId = androidAppId;
	}
	public int getPartnerId(){
		return this.mPartnerId;
	}
	
}
