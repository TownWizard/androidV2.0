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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
    
    public void executeSearch() {
        String searchRequest = null;
        if (mInputEditText.getText().toString().equals("")) {
            searchRequest = "lat=" + CurrentLocation.latitude() + "&lon=" + CurrentLocation.longitude();
            Log.d("Latitude", Double.toString(CurrentLocation.latitude()));
            Log.d("Longitude", Double.toString(CurrentLocation.longitude()));
        } else {
            searchRequest = "q=" + mInputEditText.getText().toString();

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

}