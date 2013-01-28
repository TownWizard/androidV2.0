package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.Category;
import com.townwizard.android.category.SearchCategories;
import com.townwizard.android.utils.DownloadImageHelper;
import com.townwizard.android.utils.TownWizardConstants;

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
        String imageUrl = extras.getString(TownWizardConstants.IMAGE_URL);
        if (imageUrl.length() > 0) {
            ImageView iv = (ImageView) findViewById(R.id.iv_categories_header);
            new DownloadImageHelper(iv).execute(TownWizardConstants.CONTAINER_SITE + imageUrl);
        }        
        
        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this);        
        final String[] params = {
                extras.getString(TownWizardConstants.PARTNER_ID),
                extras.getString(TownWizardConstants.URL)
        };
        
        listView.setAdapter(categoriesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Category category = (Category) categoriesAdapter.getItem(position);
                    String siteUrl = params[1];
                    String categoryUrl  = category.getUrl().startsWith("http") ? 
                            category.getUrl() : siteUrl + category.getUrl();
                    
                    if(Category.ViewType.JSON.equals(category.getViewType())) {
                        startJsonActivity(siteUrl, categoryUrl, category);
                    } else {
                        startWebActivity(siteUrl, categoryUrl, category.getName());
                    }
                }
            }
        );
        
        new SearchCategories(this, categoriesAdapter).execute(params);
    }

    private void startWebActivity(String siteUrl, String categoryUrl, String name) {
        Intent web = new Intent(this, WebActivity.class);
        web.putExtra(TownWizardConstants.URL_SITE, siteUrl);
        web.putExtra(TownWizardConstants.URL_SECTION, categoryUrl);
        web.putExtra(TownWizardConstants.CATEGORY_NAME, name);
        startActivity(web);
    }
    
    private void startJsonActivity(String siteUrl, String categoryUrl, Category category) {        
        Class<? extends Activity> activityClass = category.getJsonViewActivityClass();
        Intent i = new Intent(this, activityClass);
        i.putExtra(TownWizardConstants.URL_SITE, siteUrl);
        i.putExtra(TownWizardConstants.URL_SECTION, categoryUrl);
        i.putExtra(TownWizardConstants.CATEGORY_NAME, category.getName());
        startActivity(i);
    }

}
