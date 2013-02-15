package com.townwizard.android;

import java.util.concurrent.ExecutionException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.CategoriesLoadTask;
import com.townwizard.android.config.Constants;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.partner.PartnersAdapter;
import com.townwizard.android.partner.SearchPartners;
import com.townwizard.android.utils.CurrentLocation;

public class TownWizardActivity extends ListActivity {
    
    private ImageButton mSearchButton;
    private ImageButton mClearButton;
    private PartnersAdapter mListAdapter;
    private EditText mInputEditText;
    private int mOffset = 0;
    private View.OnClickListener mOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.partners);

        mListAdapter = new PartnersAdapter(getApplicationContext(), R.id.name);
        setListAdapter(mListAdapter);

        mInputEditText = (EditText) findViewById(R.id.et_input);
        mSearchButton = (ImageButton) findViewById(R.id.bt_search);        
        mClearButton = (ImageButton) findViewById(R.id.bt_clear_edittext);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        mClearButton.setVisibility(View.INVISIBLE);
        

        mOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_search: {
                        mListAdapter.clearSearchList();
                        mOffset = 0;
                        executeSearch();
                        break;
                    }
                    case R.id.bt_clear_edittext: {
                        mInputEditText.setText("");
                        mClearButton.setVisibility(View.INVISIBLE);
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

        mSearchButton.setOnClickListener(mOnClickListener);        
        mClearButton.setOnClickListener(mOnClickListener);
        searchButton.setOnClickListener(mOnClickListener);

        mInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mClearButton.setVisibility(View.VISIBLE);
                } else {
                    mClearButton.setVisibility(View.INVISIBLE);
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Partner item = (Partner) getListAdapter().getItem(position);

        if (item.getName().equals("Load more")) {
            mListAdapter.removeItem(mListAdapter.getCount() - 1);
            executeSearch();
        } else {
            if (item.getAndroidAppId().length() == 0) {
                CategoriesLoadTask categoriesLoader = new CategoriesLoadTask(this);
                categoriesLoader.execute(new String[] {Integer.valueOf(item.getId()).toString()});
                CategoriesAdapter categoriesAdapter = categoriesLoader.getCategoriesAdapter();
                
                Intent web = new Intent(this, WebActivity.class);
                web.putExtra(Constants.URL_SITE, item.getUrl());
                String categoryUrl = getFullCategoryUrl(item.getUrl(), categoriesAdapter.getHomeUrl());
                web.putExtra(Constants.URL_SECTION, categoryUrl);
                web.putExtra(Constants.CATEGORY_NAME, Constants.HOME);
                web.putExtra(Constants.PARTNER_NAME, item.getName());
                web.putExtra(Constants.PARTNER_ID, Integer.valueOf(item.getId()).toString());
                web.putExtra(Constants.IMAGE_URL, item.getImageUrl());
                startActivity(web);
            } else {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + item.getAndroidAppId()));
                startActivity(browseIntent);
            }
        }
    }
    
    private String getFullCategoryUrl(String siteUrl, String url) {
        return url.startsWith("http") ? url : siteUrl + url;        
    }    

    public void executeSearch() {
        String searchRequest = null;
        if (mInputEditText.getText().toString().equals("")) {
            searchRequest = "lat=" + CurrentLocation.latitude() + "&lon=" + CurrentLocation.longitude();
            Log.d("Latitude", Double.toString(CurrentLocation.latitude()));
            Log.d("Longitude", Double.toString(CurrentLocation.longitude()));
        } else {
            searchRequest = "q=" + mInputEditText.getText().toString();

        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInputEditText.getWindowToken(), 0);
        String[] params = { searchRequest, Integer.toString(mOffset) };
        try {
            mOffset = new SearchPartners(this, mListAdapter).execute(params).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}