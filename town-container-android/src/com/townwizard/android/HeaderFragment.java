package com.townwizard.android;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.townwizard.android.config.Constants;

public class HeaderFragment extends Fragment {
    
    private View header;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        
        header = inflater.inflate(R.layout.header, container, false);        
        FragmentActivity activity = getActivity();
        Bundle extras = activity.getIntent().getExtras();
        TextView headerCategoryView = (TextView) header.findViewById(R.id.tv_header_web);
        headerCategoryView.setText(extras.getString(Constants.CATEGORY_NAME));
        TextView headerPartnerView = (TextView) header.findViewById(R.id.header_partner_name);
        headerPartnerView.setText(extras.getString(Constants.PARTNER_NAME));
        drawBackButton(activity);
        return header;
    }

    public static final void drawBackButton(FragmentActivity activity) {
        drawBackButton(activity, null);
    }
    
    public static final void drawBackButton(FragmentActivity activity, WebView webView) {
        Fragment header = activity.getSupportFragmentManager().findFragmentById(R.id.header_fragment);
        if(header != null) {
            ((HeaderFragment) header).drawBackBtn(activity, webView);
        }
    }
    
    private void drawBackBtn (final Activity activity, final WebView webView) {
        LinearLayout backButtonArea = (LinearLayout)header.findViewById(R.id.header_back_button);
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layout = (webView != null && webView.canGoBack()) ? 
                R.layout.back_button : R.layout.back_button_root;
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
    
    private void startCategoriesActivity(Activity activity) {
        Bundle extras = activity.getIntent().getExtras();
        Intent categories = new Intent(activity, CategoriesActivity.class);        
        categories.putExtra(Constants.PARTNER_ID, extras.getString(Constants.PARTNER_ID));
        categories.putExtra(Constants.PARTNER_NAME, extras.getString(Constants.PARTNER_NAME));
        categories.putExtra(Constants.URL, extras.getString(Constants.URL_SITE));
        categories.putExtra(Constants.IMAGE_URL, extras.getString(Constants.IMAGE_URL));
        startActivity(categories);
    }    
    
    
    private void goBack(Activity activity, WebView webView) {
        if(webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            Bundle extras = activity.getIntent().getExtras();
            Serializable klass = extras.getSerializable(Constants.FROM_ACTIVITY);
            if(klass != null) {
                activity.finish();
            } else {
                startCategoriesActivity(activity);
            }
        }
    }
}
