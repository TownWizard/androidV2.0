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

import com.townwizard.android.R;
import com.townwizard.android.model.Partner;
import com.townwizard.android.ui.adapter.PartnersAdapter;
import com.townwizard.android.utils.CurrentLocation;
import com.townwizard.android.utils.SearchPartners;
import com.townwizard.android.utils.TownWizardConstants;

public class TownWizardActivity extends ListActivity {
    /** Called when the activity is first created. */
    private ImageButton mSearchButton;
    private ImageButton mInfoButton;


    private ImageButton mClearButton;
    private PartnersAdapter mListAdapter;
    private EditText mInputEditText;
    private int mOffset = 0;
    private View.OnClickListener mOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mListAdapter = new PartnersAdapter(getApplicationContext(), R.id.name);
        setListAdapter(mListAdapter);

        mInputEditText = (EditText) findViewById(R.id.et_input);
        mSearchButton = (ImageButton) findViewById(R.id.bt_search);
        mInfoButton = (ImageButton) findViewById(R.id.bt_info);
        mClearButton = (ImageButton) findViewById(R.id.bt_clear_edittext);
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
                    case R.id.bt_info: {

                        Intent web = new Intent(getApplicationContext(), WebActivity.class);
                        web.putExtra(TownWizardConstants.URL_SITE, "http://www.townwizard.com/");
                        web.putExtra(TownWizardConstants.URL_SECTION, "app-info");
                        web.putExtra(TownWizardConstants.PARTNER_NAME, "info");
                        startActivity(web);
                        break;
                    }
                    case R.id.bt_clear_edittext: {
                        mInputEditText.setText("");
                        mClearButton.setVisibility(View.INVISIBLE);
                        break;
                    }

                }
            }
        };

        mSearchButton.setOnClickListener(mOnClickListener);
        mInfoButton.setOnClickListener(mOnClickListener);
        mClearButton.setOnClickListener(mOnClickListener);

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

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Partner item = (Partner) getListAdapter().getItem(position);

        if (item.getName().equals("Load more")) {
            mListAdapter.removeItem(mListAdapter.getCount() - 1);
            executeSearch();
        } else {
            if (item.getAndroidAppId().length() == 0) {
                Intent categories = new Intent(this, CategoriesActivity.class);
                categories.putExtra(TownWizardConstants.PARTNER_NAME, item.getName());
                categories.putExtra(TownWizardConstants.PARTNER_ID, Integer.toString(item.getPartnerId()));
                categories.putExtra(TownWizardConstants.URL, item.getUrl());
                if (item.getImageUrl().length() > 0) {
                    categories.putExtra(TownWizardConstants.IMAGE_URL, item.getImageUrl());
                } else {
                    categories.putExtra(TownWizardConstants.IMAGE_URL, "");
                }
                startActivity(categories);
            } else {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + item.getAndroidAppId()));
                startActivity(browseIntent);
            }
        }
    }

    public void executeSearch() {
        String searchRequest = "q=";

        if (mInputEditText.getText().toString().equals("")) {

            searchRequest += "&lat=" + CurrentLocation.sLatitude + "&lon=" + CurrentLocation.sLongitude;
            Log.d("Latitude", Double.toString(CurrentLocation.sLatitude));
            Log.d("Longitude", Double.toString(CurrentLocation.sLongitude));
        } else {
            searchRequest += mInputEditText.getText().toString();

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