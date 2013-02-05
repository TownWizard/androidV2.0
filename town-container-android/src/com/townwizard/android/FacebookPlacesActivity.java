package com.townwizard.android;

import com.townwizard.android.utils.TownWizardConstants;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FacebookPlacesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_places);
        Bundle extras = getIntent().getExtras();
        
        TextView mTextView = (TextView) findViewById(R.id.tv_header_web);
        mTextView.setText(extras.getString(TownWizardConstants.CATEGORY_NAME));
        
        drawBackButton();
    }
    
    private void drawBackButton() {
        LinearLayout backButtonArea = (LinearLayout)findViewById(R.id.header_back_button);        
        LayoutInflater inflater = LayoutInflater.from(this);
        int layout = R.layout.back_button_root;
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
      onBackPressed();
    }
}
