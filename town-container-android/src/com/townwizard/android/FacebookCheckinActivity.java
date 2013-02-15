package com.townwizard.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.townwizard.android.config.Constants;

public class FacebookCheckinActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_checkin);
    
        Bundle extras = getIntent().getExtras();
        String placeId = extras.getString(Constants.FB_PLACE_ID);

        final EditText statusEditText = (EditText) findViewById(R.id.facebook_status);
        
        ImageButton postButton = (ImageButton) findViewById(R.id.facebook_status_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String message = statusEditText.getText().toString();
                postCheckin(message);

                
            }
        });
        
        
        


    }
    
    private void postCheckin(String message) {
        
    }


}
