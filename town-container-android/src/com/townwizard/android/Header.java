package com.townwizard.android;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.townwizard.android.category.Category;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.utils.CurrentLocation;
import com.townwizard.android.utils.ServerConnector;

public class Header {
    
    private View headerView;
    private Activity activity;
    private WebView webView;
    private Stack<String> webViewBreadCrumb;

    public static final Header build(Activity activity) {
        return build(activity, null);
    }    
    
    public static final Header build(Activity activity, WebView webView) {
        return new Header(activity, webView);
    }
    
    private Header(Activity activity, WebView webView) {
        this.activity = activity;
        if(webView != null) {
            this.webView = webView;
            webViewBreadCrumb = new Stack<String>();
        }
        
        build();
    }
    
    private void build() {
        View rootView = activity.getWindow().getDecorView().getRootView(); 
        View header = rootView.findViewById(R.id.header);
        TextView headerCategoryView = (TextView) header.findViewById(R.id.tv_header_web);
        Config config = Config.getConfig(activity);
        Category c = config.getCategory();
        if(c != null) {
            // bhavan: use display name: headerCategoryView.setText(c.getName());
            headerCategoryView.setText(c.getDisplayName());
        }
        TextView headerPartnerView = (TextView) header.findViewById(R.id.header_partner_name);        
        String partnerName = getPartnerName();
        if(partnerName != null) {
            headerPartnerView.setText(partnerName);
        }
        headerView = header;
        drawBackButton();
    }
    
    public void drawBackButton () {        
        LinearLayout backButtonArea = (LinearLayout)headerView.findViewById(R.id.header_back_button);
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layout = getBackButtonLayout();
        View backButton = inflater.inflate(layout, backButtonArea, false);
        backButtonArea.removeAllViews();
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
    
    public void goBack() {
        if(webView != null && canWebViewGoBack()) {
            webView.loadUrl(webViewBreadCrumb.pop());
        } else {
            if(getFromActivityClass() != null) {
                activity.finish();
            } else {
                startCategoriesActivity(activity);
            }
        }
    }
    
    public void addToBreadCrumb(String currentUrl, String nextUrl) {
        if(validForBreadCrumb(nextUrl)) {
            if(!webViewBreadCrumb.contains(currentUrl)) {
                webViewBreadCrumb.push(currentUrl);
            }
        }
    }
    
    private boolean canWebViewGoBack() {
        return !webViewBreadCrumb.isEmpty();
    }
    
    private boolean validForBreadCrumb(String url) {
        for(String s : BREADCRUMB_EXCLUDES) {
            if(url.contains(s)) return false;
        }
        return true;
    }
    
    private int getBackButtonLayout() {
        if(webView != null) {
            return (canWebViewGoBack() ? R.layout.back_button : R.layout.back_button_root);
        }
        String activityClassName = activity.getClass().getName(); 
        return (activityClassName.contains("Facebook") || activityClassName.contains("MapView")) ?
                R.layout.back_button : R.layout.back_button_root;
    }
    
    private String getPartnerName() {
        Partner partner = Config.getConfig(activity).getPartner();
        if(partner != null) {
            String pName = partner.getName();
            if(Constants.CONTENT_PARTNER_EVENTS.equals(pName) ||
               Constants.CONTENT_PARTNER_PLACES.equals(pName) ||
               Constants.CONTENT_PARTNER_RESTAURANTS.equals(pName)) {
                String zip = Config.getConfig(activity).getZip();
                if(zip == null) {
                    String zipCodeUrl = 
                            partner.getUrl() +
                            Config.CONTENT_PARTNER_ZIP_CODE_URL + 
                            "?lat=" + CurrentLocation.latitude() +
                            "&lon=" + CurrentLocation.longitude();
                    try {
                        String zipFromUrl = ServerConnector.getServerResponse(new URL(zipCodeUrl));
                        if(zipFromUrl != null && !"".equals(zipFromUrl)) {
                            Config.getConfig(activity).setZip(zipFromUrl);
                            zip = zipFromUrl;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                
                if(zip != null) {
                    Category c = Config.getConfig(activity).getCategory();
                    if(c != null) {
                        // bhavan: use display name: String cName = c.getName();
                        //String cName = c.getName();
                        String cName = c.getDisplayName();
                        String newPartnerName = null;
                        if(Constants.EVENTS.equals(cName)) {
                            newPartnerName = Constants.CONTENT_PARTNER_EVENTS_DISPLAY;
                        } else if(Constants.RESTAURANTS.equals(cName)) {
                            newPartnerName = Constants.CONTENT_PARTNER_RESTAURANTS_DISPLAY;
                        } else if(Constants.PLACES.equals(cName)) {
                            newPartnerName = Constants.CONTENT_PARTNER_PLACES_DISPLAY;
                        }
                        if(newPartnerName != null) {
                            newPartnerName += " " + zip;
                            pName = newPartnerName;
                        }
                    } else {
                        pName = Constants.CONTENT_PARTNER_EVENTS;
                    }
                } else {
                    pName = Constants.CONTENT_PARTNER_EVENTS;
                }
            }
            return pName;
        }
        return null;
    }
    
    private static final List<String> BREADCRUMB_EXCLUDES = new ArrayList<String>();
    static {
        BREADCRUMB_EXCLUDES.add(Constants.TW_DB_API);
        BREADCRUMB_EXCLUDES.add(Constants.YOUTUBE);
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
}