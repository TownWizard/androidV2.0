package com.townwizard.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.townwizard.android.R;
import com.townwizard.android.model.Categories;
import com.townwizard.android.ui.adapter.CategoriesAdapter;
import com.townwizard.android.utils.DownloadImageHelper;
import com.townwizard.android.utils.SearchCategories;
import com.townwizard.android.utils.TownWizardConstants;

public class CategoriesActivity extends Activity {
    private CategoriesAdapter mCategoriesAdapter;
    private String mPartnerName;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.categories);
        mCategoriesAdapter = new CategoriesAdapter(this);
        Bundle extras = getIntent().getExtras();
        final String[] params = {
                extras.getString(TownWizardConstants.PARTNER_ID),
                extras.getString(TownWizardConstants.URL) };
        mPartnerName = extras.getString(TownWizardConstants.PARTNER_NAME);
        mImageUrl = extras.getString(TownWizardConstants.IMAGE_URL);
        ImageView iv = (ImageView) findViewById(R.id.iv_categories_header);
        Log.d("imageUrl", mImageUrl);
        if (mImageUrl.length() > 0) {
            new DownloadImageHelper(iv).execute(TownWizardConstants.CONTAINER_SITE + mImageUrl);
        }
        GridView gridView = (GridView) findViewById(R.id.gridView);
        TextView tv = (TextView) findViewById(R.id.tv_categories_header);
        tv.setText(mPartnerName);
        gridView.setAdapter(mCategoriesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Categories item = (Categories) mCategoriesAdapter.getItem(position);
                Log.d("url", params[1] + item.getUrl());
                startBrowser(params[1], item.getUrl(), mPartnerName + " - "
                        + item.getName());
            }
        });
        new SearchCategories(mCategoriesAdapter).execute(params);
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
