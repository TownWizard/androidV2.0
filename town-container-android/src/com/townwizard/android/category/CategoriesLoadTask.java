package com.townwizard.android.category;

import java.util.List;


import android.content.Context;
import android.os.AsyncTask;


public class CategoriesLoadTask extends AsyncTask<String, Category, Void> {
       
    private CategoriesAdapter categoriesAdapter;
    private Context context;    

    private CategoriesLoadTask(Context context) {
        this.context = context;
        this.categoriesAdapter = new CategoriesAdapter(context);
    }
    
    public static final CategoriesAdapter loadCategories(Context context, String partnerId, boolean async) {
        CategoriesLoadTask categoryLoader = new CategoriesLoadTask(context);
        if(async) {
            categoryLoader.execute(new String[]{partnerId});
        } else {
            categoryLoader.loadCategories(partnerId);
        }
        return categoryLoader.categoriesAdapter;
    }
    
    @Override
    protected void onProgressUpdate(Category ... values) {
    	super.onProgressUpdate(values);
    	categoriesAdapter.addItem(values[0]);    	
    }

    @Override
    protected Void doInBackground(String ... params) {
        doLoadCategories(params[0], new LoadedCategoryHandler() {
            @Override
            public void handleCategory(Category c) {
                publishProgress(c);
            }
        });
        return null;
    }
    
    private void loadCategories(String partnerId) {
        doLoadCategories(partnerId, new LoadedCategoryHandler() {
            @Override
            public void handleCategory(Category c) {
                categoriesAdapter.addItem(c);
            }
        });        
    }   
    
    private void doLoadCategories(String partnerId, LoadedCategoryHandler handler) {        
        try {
            List<Category> categories = Category.getCategories(context, partnerId);
            for(Category c : categories) {
                handler.handleCategory(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private static interface LoadedCategoryHandler {
        void handleCategory(Category c);
    }

}