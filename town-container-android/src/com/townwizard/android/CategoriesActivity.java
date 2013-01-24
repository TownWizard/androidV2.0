package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
        final String partnerName = extras.getString(TownWizardConstants.PARTNER_NAME);
        final String[] params = {
                extras.getString(TownWizardConstants.PARTNER_ID),
                extras.getString(TownWizardConstants.URL)
        };
        
        listView.setAdapter(categoriesAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Category item = (Category) categoriesAdapter.getItem(position);                
                    startBrowser(params[1], item.getUrl(), partnerName + " - " + item.getName());
                }
            }
        );
        
        new SearchCategories(this, categoriesAdapter).execute(params);
    }

    protected void startBrowser(String urlSite, String urlSection, String name) {
        Intent web = new Intent(this, WebActivity.class);
        web.putExtra(TownWizardConstants.URL_SITE, urlSite);
        web.putExtra(TownWizardConstants.URL_SECTION, urlSection);

        ImageView iv = (ImageView) findViewById(R.id.iv_categories_header);
        Drawable drawable = iv.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        web.putExtra(TownWizardConstants.HEADER_IMAGE, bitmap);
        web.putExtra(TownWizardConstants.PARTNER_NAME, name);
        startActivity(web);
    }

}
