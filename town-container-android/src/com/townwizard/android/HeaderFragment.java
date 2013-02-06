package com.townwizard.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.townwizard.android.utils.TownWizardConstants;

public class HeaderFragment extends Fragment {
    
    private View header;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        
        header = inflater.inflate(R.layout.header, container, false);
        TextView mTextView = (TextView) header.findViewById(R.id.tv_header_web);
        FragmentActivity activity = getActivity();
        Bundle extras = activity.getIntent().getExtras();
        String categoryName = extras.getString(TownWizardConstants.CATEGORY_NAME);
        mTextView.setText(categoryName);
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
    
    private void goBack(Activity activity, WebView webView) {
        if(webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            activity.onBackPressed();
        }
    }
}
