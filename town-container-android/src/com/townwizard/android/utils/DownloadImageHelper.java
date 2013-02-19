package com.townwizard.android.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class DownloadImageHelper extends BitmapDownloaderTask {
    
    private ImageView mImageView;

    public DownloadImageHelper(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            if (mImageView != null) {
                mImageView.setImageBitmap(result);
            }
        }
    }
}