package com.townwizard.android.model;

import android.graphics.Bitmap;

public class Category {
	private Bitmap mImage;
	private String mName;
	private String mUrl;
	
	public Category(Bitmap image, String name, String url){
		mImage = image;
		mName = handleSpecialChars(name);
		mUrl = url;
	}
	
	public Bitmap getImage(){
		return mImage;
	}
	public String getName(){
		return mName;
	}
	public String getUrl(){
		return mUrl;
	}
	
	private String handleSpecialChars(String name) {
	    if(name == null || "".equals(name))
	        return null;
	    
	    if(name.contains("&amp;")) {
	        return name.replace("&amp;", "&");
	    }
	    
	    return name;
	}
}
