package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;

public class Header {
    
    private Activity activity;
    private View headerView;

    public static final Header build(Activity activity) {
        return new Header(activity);
    }
    
    private Header(Activity activity) {
        this.activity = activity;
        build();
    }
    
    private void build() {
        View rootView = activity.getWindow().getDecorView().getRootView(); 
        View header = rootView.findViewById(R.id.header);
        TextView headerCategoryView = (TextView) header.findViewById(R.id.tv_header_web);
        Config config = Config.getConfig(activity);
        headerCategoryView.setText(config.getCategory().getName());
        TextView headerPartnerView = (TextView) header.findViewById(R.id.header_partner_name);
        headerPartnerView.setText(config.getPartner().getName());
        headerView = header;
        drawBackButton(null);
    }
    
    public void drawBackButton (final WebView webView) {        
        LinearLayout backButtonArea = (LinearLayout)headerView.findViewById(R.id.header_back_button);
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layout = getBackButtonLayout(webView);
        View backButton = inflater.inflate(layout, backButtonArea, false);
        backButtonArea.removeAllViews();
        backButtonArea.addView(backButton);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goBack(activity, webView);
                    }
                }
        );
    }
    
    private int getBackButtonLayout(WebView webView) {
        if(webView != null) {
            return (webView.canGoBack() ? R.layout.back_button : R.layout.back_button_root);
        }
        String activityClassName = activity.getClass().getName(); 
        return (activityClassName.contains("Facebook") || activityClassName.contains("MapView")) ?
                R.layout.back_button : R.layout.back_button_root;
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends Activity> getFromActivityClass() {
        Bundle extras = activity.getIntent().getExtras();
        if(extras != null) {
            return (Class<? extends Activity>) extras.getSerializable(Constants.FROM_ACTIVITY);
        }
        return null;
    }
    
    private void startCategoriesActivity(Activity activity) {
        activity.startActivity(new Intent(activity, CategoriesActivity.class));
    }
    
    private void goBackToVideos() {
        Intent web = new Intent(activity, WebActivity.class);
        web.putExtra(Constants.OVERRIDE_TRANSITION, true);
        activity.startActivity(web);
    }

    private void goBack(Activity activity, WebView webView) {
        if(webView != null && webView.canGoBack()) {
            if(Constants.VIDEOS.equals(Config.getConfig(activity).getCategory().getName())) {
                goBackToVideos();
            } else {
                webView.goBack();
            }
        } else {
            if(getFromActivityClass() != null) {
                activity.finish();
            } else {
                startCategoriesActivity(activity);
            }
        }
    }
}