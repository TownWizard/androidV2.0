package com.townwizard.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public abstract class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    
    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }
    
    @Override
    protected abstract void onPostExecute(Bitmap result);
    
    private Bitmap downloadBitmap(String url) {
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) in.close();
            } catch (IOException e) {
                //nothing
            }
        }
        return null;        
    }
}
