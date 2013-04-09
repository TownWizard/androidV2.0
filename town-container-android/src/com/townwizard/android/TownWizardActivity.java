package com.townwizard.android;


import java.util.concurrent.ExecutionException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.CategoriesLoadTask;
import com.townwizard.android.config.Config;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.partner.PartnersAdapter;
import com.townwizard.android.partner.SearchPartners;
import com.townwizard.android.utils.CurrentLocation;
import com.townwizard.android.utils.Utils;

/**
 * Activity for searching partners by name and displaying them in a list
 */
public class TownWizardActivity extends ListActivity {

    private PartnersAdapter mListAdapter;
    private EditText mInputEditText;
    private int mOffset = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.partners);
        
        ImageView headerImageView = (ImageView)findViewById(R.id.partners_header);
        Bitmap headerImage = BitmapFactory.decodeResource(getResources(), R.drawable.search_header);
        Utils.setScaledToScreenBitmap(headerImageView, headerImage);
        
        Utils.checkConnectivity(this);

        mListAdapter = new PartnersAdapter(getApplicationContext(), R.id.name);
        setListAdapter(mListAdapter);

        mInputEditText = (EditText) findViewById(R.id.search_text_input);
        final ImageButton goButton = (ImageButton) findViewById(R.id.go_button);
        final ImageButton clearButton = (ImageButton) findViewById(R.id.clear_text_button);
        final ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        clearButton.setVisibility(View.INVISIBLE);
        
        OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.go_button: {
                        mListAdapter.clearSearchList();
                        mOffset = 0;
                        executeSearch();
                        break;
                    }
                    case R.id.clear_text_button: {
                        mInputEditText.setText("");
                        clearButton.setVisibility(View.INVISIBLE);
                        Utils.hideScreenKeyboard(mInputEditText, TownWizardActivity.this);
                        break;
                    }
                    case R.id.search_button: {
                        InputMethodManager imm = 
                                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        mInputEditText.requestFocus();
                        break;
                    }
                }
            }
        };

        goButton.setOnClickListener(mOnClickListener);        
        clearButton.setOnClickListener(mOnClickListener);
        searchButton.setOnClickListener(mOnClickListener);

        mInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        executeSearch();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Partner item = (Partner) getListAdapter().getItem(position);

        if (item.getName().equals("Load more")) {
            mListAdapter.removeItem(mListAdapter.getCount() - 1);
            executeSearch();
        } else {
            if (item.getAndroidAppId().length() == 0) {
                CategoriesAdapter categoriesAdapter = 
                        CategoriesLoadTask.loadCategories(this, Integer.valueOf(item.getId()).toString());
                
                Intent web = new Intent(this, WebActivity.class);
                Config.getConfig(this).setPartner(item);
                Config.getConfig(this).setCategory(categoriesAdapter.getHomeCategory());
                startActivity(web);
            } else {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + item.getAndroidAppId()));
                startActivity(browseIntent);
            }
        }
    }
    
    private void executeSearch() {
        if(!Utils.isOnline(this)) return;

        String input = mInputEditText.getText().toString();
        saveSearchZip(input);
        
        String searchRequest = null;
        if (input == null || input.length() == 0) {
            searchRequest = "lat=" + CurrentLocation.latitude() + "&lon=" + CurrentLocation.longitude();
        } else {
            searchRequest = "q=" + input;
        }

        Utils.hideScreenKeyboard(mInputEditText, this);

        String[] params = { searchRequest, Integer.toString(mOffset) };
        try {
            mOffset = new SearchPartners(this, mListAdapter).execute(params).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    private void saveSearchZip(String searchRequest) {
        if(isZip(searchRequest)) {
            Config.getConfig(this).setZip(searchRequest.split("-")[0]);
        } else {
            Config.getConfig(this).setZip(null);
        }
    }
    
    private boolean isZip(String s) {
        return s.matches("^\\d{5}(-\\d{4})?$");
    }

}