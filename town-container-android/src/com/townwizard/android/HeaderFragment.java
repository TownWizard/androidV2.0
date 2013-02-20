package com.townwizard.android;

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

import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;

public class HeaderFragment extends Fragment {
    
    private View header;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        
        header = inflater.inflate(R.layout.header, container, false);        
        FragmentActivity activity = getActivity();        
        TextView headerCategoryView = (TextView) header.findViewById(R.id.tv_header_web);
        Config config = Config.getConfig(getActivity());
        headerCategoryView.setText(config.getCategory().getName());
        TextView headerPartnerView = (TextView) header.findViewById(R.id.header_partner_name);
        headerPartnerView.setText(config.getPartner().getName());
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
        return getActivity().getClass().getName().contains("Facebook") ?
                R.layout.back_button : R.layout.back_button_root;
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends Activity> getFromActivityClass() {
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) {
            return (Class<? extends Activity>) extras.getSerializable(Constants.FROM_ACTIVITY);
        }
        return null;
    }
    
    private void startCategoriesActivity(Activity activity) {
        startActivity(new Intent(activity, CategoriesActivity.class));
    }

    private void goBack(Activity activity, WebView webView) {
        if(webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            if(getFromActivityClass() != null) {
                activity.finish();
            } else {
                startCategoriesActivity(activity);
            }
        }
    }
}
