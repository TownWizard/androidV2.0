package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.Category;
import com.townwizard.android.category.SearchCategories;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.utils.DownloadImageHelper;

public class CategoriesActivity extends Activity {   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);

        ListView listView = (ListView) findViewById(R.id.category_list);
        LayoutInflater inflater = LayoutInflater.from(this);
        View headerView = inflater.inflate(R.layout.category_list_header, listView, false);
        listView.addHeaderView(headerView, null, false);        
        
        Bundle extras = getIntent().getExtras();                
        String imageUrl = extras.getString(Constants.IMAGE_URL);
        if (imageUrl.length() > 0) {
            ImageView iv = (ImageView) findViewById(R.id.iv_categories_header);
            new DownloadImageHelper(iv).execute(Config.CONTAINER_SITE + imageUrl);
        }        
        
        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this);        
        final String[] params = {
                extras.getString(Constants.PARTNER_ID),
                extras.getString(Constants.URL)
        };
        final String siteUrl = params[1];
        final String partnerName = extras.getString(Constants.PARTNER_NAME);
        
        TextView aboutButton = (TextView) findViewById(R.id.button_about);
        aboutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String aboutUsUrl = categoriesAdapter.getAboutUsUrl();
                        if(aboutUsUrl == null) {
                            aboutUsUrl = Config.DEFAULT_ABOUT_US_URI;
                        }
                        String categoryUrl = getFullCategoryUrl(siteUrl, aboutUsUrl);
                        startWebActivity(siteUrl, categoryUrl, CategoriesAdapter.ABOUT_US, partnerName);
                    }
                }
        );
        
        TextView changeButton = (TextView) findViewById(R.id.button_change);
        changeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CategoriesActivity.this, TownWizardActivity.class));
                    }
                }
        );        
        
        listView.setAdapter(categoriesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Category category = (Category) categoriesAdapter.getItem(position);                    
                    String categoryUrl = getFullCategoryUrl(siteUrl, category.getUrl());
                    if(Category.ViewType.JSON.equals(category.getViewType())) {
                        startJsonActivity(siteUrl, categoryUrl, category, partnerName);
                    } else {
                        startWebActivity(siteUrl, categoryUrl, category.getName(), partnerName);
                    }
                }
            }
        );
        
        new SearchCategories(this, categoriesAdapter).execute(params);
    }
    
    private String getFullCategoryUrl(String siteUrl, String url) {
        return url.startsWith("http") ? url : siteUrl + url;        
    }

    private void startWebActivity(String siteUrl, String categoryUrl, String name, String partnerName) {
        Intent web = new Intent(this, WebActivity.class);
        web.putExtra(Constants.URL_SITE, siteUrl);
        web.putExtra(Constants.URL_SECTION, categoryUrl);
        web.putExtra(Constants.CATEGORY_NAME, name);
        web.putExtra(Constants.PARTNER_NAME, partnerName);
        startActivity(web);
    }
    
    private void startJsonActivity(String siteUrl, String categoryUrl, Category category, String partnerName) {        
        Class<? extends Activity> activityClass = category.getJsonViewActivityClass();
        Intent i = new Intent(this, activityClass);
        i.putExtra(Constants.URL_SITE, siteUrl);
        i.putExtra(Constants.URL_SECTION, categoryUrl);
        i.putExtra(Constants.CATEGORY_NAME, category.getName());
        i.putExtra(Constants.PARTNER_NAME, partnerName);
        startActivity(i);
    }

}
