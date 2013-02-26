package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.townwizard.android.category.CategoriesAdapter;
import com.townwizard.android.category.CategoriesLoadTask;
import com.townwizard.android.category.Category;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.utils.BitmapDownloaderTask;

/**
 * Displays the menu (list of categories) screen.
 */
public class CategoriesActivity extends Activity {   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_from_left);
        setContentView(R.layout.categories);
        
        Config config = Config.getConfig(this);
        boolean isContainerApp = config.isContainerApp(); 
        Partner partner = config.getPartner();
        
        CategoriesAdapter categoriesAdapter = 
                CategoriesLoadTask.loadCategories(this, Integer.valueOf(partner.getId()).toString());
        
        buildCategoriesList(isContainerApp, categoriesAdapter);
        
        loadPartnerImage(partner);
        
        if(isContainerApp) {
            buildAboutAndChangeButtons(categoriesAdapter);
        }
    }
    
    private void buildCategoriesList(boolean isContainerApp, final CategoriesAdapter categoriesAdapter) {
        int headerViewId = isContainerApp ? R.layout.category_list_header_c :
            R.layout.category_list_header_p;        
        ListView listView = (ListView) findViewById(R.id.category_list);
        View headerView = LayoutInflater.from(this).inflate(headerViewId, listView, false);
        listView.addHeaderView(headerView, null, false);
        listView.setAdapter(categoriesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Category category = (Category) categoriesAdapter.getItem(position);
                    Config.getConfig(CategoriesActivity.this).setCategory(category);
                    if(Category.ViewType.JSON.equals(category.getViewType())) {
                        startJsonActivity(category);
                    } else {
                        startWebActivity();
                    }
                }
            }
        );
    }    
    
    private void loadPartnerImage(Partner partner) {
        String imageUrl = partner.getImageUrl();
        if (imageUrl.length() > 0) {
            final ImageView iv = (ImageView) findViewById(R.id.categories_header);
            new BitmapDownloaderTask() {
                @Override
                protected void onPostExecute(Bitmap result) {
                    if (result != null) {
                        iv.setImageBitmap(result);                        
                    }
                }                
            }.execute(Config.CONTAINER_SITE + imageUrl);
        }
    }
    
    private void buildAboutAndChangeButtons(final CategoriesAdapter categoriesAdapter) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.button_about) {
                    Config.getConfig(CategoriesActivity.this).setCategory(
                            categoriesAdapter.getAboutUsCategory());
                    startWebActivity();
                } else {
                    startActivity(new Intent(CategoriesActivity.this, TownWizardActivity.class));
                }
            }
        };
        
        TextView aboutButton = (TextView) findViewById(R.id.button_about);
        TextView changeButton = (TextView) findViewById(R.id.button_change);
        aboutButton.setOnClickListener(listener);
        changeButton.setOnClickListener(listener);
    }

    private void startWebActivity() {
        Intent web = new Intent(this, WebActivity.class);
        web.putExtra(Constants.FROM_ACTIVITY, getClass());
        startActivity(web);
    }
    
    private void startJsonActivity(Category category) {        
        Class<? extends Activity> activityClass = category.getJsonViewActivityClass();
        if(activityClass != null) {
            startActivity(new Intent(this, activityClass));
        }
    }

}
