package com.mlsdev.android.townwizard.model;

import android.graphics.Bitmap;

public class Categories {
	private Bitmap mImage;
	private String mName;
	private String mUrl;
	
	public Categories(Bitmap image, String name, String url){
		mImage = image;
		mName = name;
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
}
