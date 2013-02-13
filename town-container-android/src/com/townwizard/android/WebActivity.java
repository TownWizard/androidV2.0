package com.townwizard.android;

import java.io.File;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.townwizard.android.config.Constants;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends Activity {

    private static final String sUpload = "components/com_shines/iuploadphoto.php";
    private String mUrlSite;
    private WebView mWebView;    
    private TextView mTextView;
    private Button mUploadButton;
    private static final int sCAMERA_RESULT = 1;
    private static final int sGALLERY = 2;
    private static Uri sImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        mUrlSite = extras.getString(Constants.URL_SITE);        
        String categoryName = extras.getString(Constants.CATEGORY_NAME);
        if (categoryName.indexOf("Photos") != -1) {
            if (isUploadScriptExist(mUrlSite + sUpload)) {
                Log.d("WebActivity", "File exist");
                setContentView(R.layout.web_with_upload);
                mUploadButton = (Button) findViewById(R.id.bt_upload);

                mUploadButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebActivity.this);
                        alertDialog.setPositiveButton("Take photo", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startCameraIntent();
                                    }
                                });
                        alertDialog.setNeutralButton("Choose from library",  new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startChooseFromLibraryIntent();
                                    }
                                });
                        alertDialog.setNegativeButton("Cancel", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    }
                });
            } else {
                setContentView(R.layout.web);
            }

        } else {
            setContentView(R.layout.web);
        }
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new TownWizardWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);        
        mTextView = (TextView) findViewById(R.id.tv_header_web);
        mTextView.setText(extras.getString(Constants.CATEGORY_NAME));
        TextView partnerNameView = (TextView) findViewById(R.id.header_partner_name);
        partnerNameView.setText(extras.getString(Constants.PARTNER_NAME));
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        
        String urlSection = extras.getString(Constants.URL_SECTION);         
        Log.d("Web Acrivity Url", urlSection);
        mWebView.loadUrl(urlSection);
    }  


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent uploadPhoto = new Intent(this, UploadPhotoActivity.class);
        uploadPhoto.putExtra(Constants.URL, mUrlSite + sUpload);

        if (requestCode == sGALLERY) {
            Log.d("result is ", Integer.toString(resultCode));

            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();

                uploadPhoto.putExtra(Constants.IMAGE_URI, imageUri);
                uploadPhoto.putExtra(Constants.SOURCE, "GALLERY");
                startActivity(uploadPhoto);

            } else {
                WebActivity.this.finish();
            }
        }
        if (requestCode == sCAMERA_RESULT) {

            if (resultCode == RESULT_OK) {
                Log.d("camera result", "start upload activity");
                Log.d("imagePath", sImagePath.toString());
                uploadPhoto.putExtra(Constants.IMAGE_URI, sImagePath);
                uploadPhoto.putExtra(Constants.SOURCE, "CAMERA");
                startActivity(uploadPhoto);
            } else {
                WebActivity.this.finish();
            }
        }
    }

    public static boolean isUploadScriptExist(String URLName) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class TownWizardWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("URL", url);
            if (url.startsWith("http")) {
                view.loadUrl(url);
            } else {
                if (url.startsWith("mailto:")) {
                    mailSend(url);
                    return true;
                }
                if (url.startsWith("tel")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(dialIntent);
                } else if (url.startsWith("APP30A:")) {
                    if (url.indexOf("SHOWMAP") != -1) {
                        showMap(url);
                    } else if (url.indexOf("FBCHECKIN") != -1) {
                        facebookCheckin();
                    }
                }
            }
            return true;
        }

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            drawBackButton();
        }
        
        @Override
        public void onPageFinished (WebView view, String url) {
            super.onPageFinished(view, url);
            drawBackButton();
        }        
    }

    private void startCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),
                Long.toString(System.currentTimeMillis()));
        sImagePath = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(cameraIntent, sCAMERA_RESULT);
    }

    private void startChooseFromLibraryIntent() {
        Intent intent = new Intent();
        intent.setType("image/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                sGALLERY);
    }

    private void showMap(String url) {
        String latlong = url.substring("APP30A:SHOWMAP:".length());
        Log.d("latlong", latlong);
        String latitude = latlong.substring(0, latlong.indexOf(":"));
        String longitude = latlong.substring(latlong.indexOf(":") + 1);
        Intent i = new Intent(WebActivity.this, MapViewActivity.class);
        i.putExtra(Constants.LATITUDE, latitude);
        i.putExtra(Constants.LONGITUDE, longitude);
        i.putExtra(Constants.CATEGORY_NAME, mTextView.getText().toString());
        startActivity(i);
    }

    private void facebookCheckin() {
        Intent i = new Intent(WebActivity.this, FacebookPlaceActivity.class);
        i.putExtra(Constants.CATEGORY_NAME, mTextView.getText().toString());
        startActivity(i);
    }

    private void mailSend(String url) {
        String mt = "mailto:?";
        String u = url;
        u = u.substring("mailto:?".length());
        u = u.replaceAll(":", "%3A");
        u = u.replaceAll("/", "%2F");
        u = u.replaceAll(" ", "%20");
        MailTo mailTo = MailTo.parse(mt + u);
        Log.d("mailTo", mailTo.toString());

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, mailTo.getTo());
        i.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
        i.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());

        startActivity(i);
    }
    
    private void drawBackButton() {
        LinearLayout backButtonArea = (LinearLayout)findViewById(R.id.header_back_button);
        backButtonArea.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        int layout = mWebView.canGoBack() ? R.layout.back_button : R.layout.back_button_root;
        View backButton = inflater.inflate(layout, backButtonArea, false);
        backButtonArea.addView(backButton);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goBack();
                    }
                }
        );        
    }
    
    private void goBack() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            Bundle extras = getIntent().getExtras();
            Serializable klass = extras.getSerializable(Constants.FROM_ACTIVITY);
            if(CategoriesActivity.class.equals(klass)) {
                finish();
            } else {
                startCategoriesActivity();
            }
        }
    }
    
    private void startCategoriesActivity() {
        Bundle extras = getIntent().getExtras();
        Intent categories = new Intent(this, CategoriesActivity.class);        
        categories.putExtra(Constants.PARTNER_ID, extras.getString(Constants.PARTNER_ID));
        categories.putExtra(Constants.PARTNER_NAME, extras.getString(Constants.PARTNER_NAME));
        categories.putExtra(Constants.URL, extras.getString(Constants.URL_SITE));
        categories.putExtra(Constants.IMAGE_URL, extras.getString(Constants.IMAGE_URL));
        startActivity(categories);
    }    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
