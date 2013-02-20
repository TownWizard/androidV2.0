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
import com.townwizard.android.category.CategoriesLoadTask;
import com.townwizard.android.category.Category;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.partner.Partner;
import com.townwizard.android.utils.DownloadImageHelper;

public class CategoriesActivity extends Activity {   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_from_left);
        setContentView(R.layout.categories);
        
        ListView listView = (ListView) findViewById(R.id.category_list);
        LayoutInflater inflater = LayoutInflater.from(this);
        
        boolean isContainerApp = Config.getConfig(this).isContainerApp(); 
        
        int headerViewId = isContainerApp ? R.layout.category_list_header_c :
            R.layout.category_list_header_p;
        
        View headerView = inflater.inflate(headerViewId, listView, false);
        listView.addHeaderView(headerView, null, false);        

        Partner partner = Config.getConfig(this).getPartner();
        String imageUrl = partner.getImageUrl();
        if (imageUrl.length() > 0) {
            ImageView iv = (ImageView) findViewById(R.id.iv_categories_header);
            new DownloadImageHelper(iv).execute(Config.CONTAINER_SITE + imageUrl);
        }        

        CategoriesLoadTask categoryLoader = new CategoriesLoadTask(this);
        categoryLoader.execute(new String[]{Integer.valueOf(partner.getId()).toString()});
        
        final CategoriesAdapter categoriesAdapter = categoryLoader.getCategoriesAdapter();                

        if(isContainerApp) {
            TextView aboutButton = (TextView) findViewById(R.id.button_about);
            aboutButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Config.getConfig(CategoriesActivity.this)
                                .setCategory(categoriesAdapter.getAboutUsCategory());
                            startWebActivity();
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
        }
        
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
