package com.mlsdev.android.townwizard.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageHelper extends AsyncTask<String, Void, Bitmap> {
    private ImageView mImageView = null;

    public DownloadImageHelper(ImageView imageView) {
        mImageView = imageView;
    }

    public DownloadImageHelper() {

    }

    // private static final String DEFAULT_URL = "http://container.mlsdev.com";
    /*
     * @Override protected void onProgressUpdate(Bitmap... values) { // TODO Auto-generated method stub
     * super.onProgressUpdate(values); ImageView iv = (ImageView) findViewById(R.id.iv_categories_header);
     * iv.setImageBitmap(values[0]); }
     */

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        String imageUrl = params[0];
        URL url = null;
        try {
            url = new URL(imageUrl);
            Log.d("IMAGE_URL", url.toString());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpURLConnection httpConnection;
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            InputStream is = httpConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            httpConnection.disconnect();
            // publishProgress(image);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mImageView != null) {
                mImageView.setImageBitmap(result);
            }
        }
    }

}