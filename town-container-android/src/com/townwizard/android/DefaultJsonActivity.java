package com.townwizard.android;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;

public class DefaultJsonActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_json);
    }
}
