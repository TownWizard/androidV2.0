package com.townwizard.android.category;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.townwizard.android.R;

public class CategoriesAdapter extends BaseAdapter {
    
    public static final String ABOUT_US = "About Us";
    
    private Context context;
        
    private Map<CategorySection, List<Category>> categories = 
            new EnumMap<CategorySection, List<Category>>(CategorySection.class);
    private List<Object> categoryList = new ArrayList<Object>();
    
    public CategoriesAdapter(Context context) {
        this.context = context;    
    }
    
    public void addItem(Category category) {        
        if(!category.hasView()) return;
        
        CategorySection section = getSection(category);
        List<Category> sectionCategories = categories.get(section);
        if(sectionCategories == null) {
            sectionCategories = new ArrayList<Category>();
            categories.put(section, sectionCategories);
        }
        sectionCategories.add(category);
        
        List<Object> categoryList = new ArrayList<Object>();
        for (Map.Entry<CategorySection, List<Category>> e : categories.entrySet()) {
            categoryList.add(e.getKey());
            for(Category c : e.getValue()) {
                categoryList.add(c);
            }
        }
        
        this.categoryList = categoryList;
        
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
    
    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER_VIEW_TYPE;
        }
        Object item = categoryList.get(position);
        if(item instanceof CategorySection) {
            return SECTION_VIEW_TYPE;
        }
        return CATEGORY_VIEW_TYPE;
    }
    
    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == CATEGORY_VIEW_TYPE;
    }    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = categoryList.get(position);
        if(item instanceof CategorySection) {
            return getSectionView((CategorySection)item, convertView, parent);
        }
        return getCategoryView((Category)item, convertView, parent);
    }
    
    public String getAboutUsUrl() {
       List<Category> helpCategories = categories.get(CategorySection.HELP);
       if(helpCategories != null) {
           for(Category c : helpCategories) {
               if(ABOUT_US.equals(c.getName())) {
                   return c.getUrl();
               }
           }
       }
       return null;
    }
    
    private View getSectionView(CategorySection section, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.category_section_header, parent, false);
        }
        TextView textView = (TextView)view.findViewById(R.id.category_section_title);
        textView.setText(section.getName());
        return view;
    }
    
    private View getCategoryView(Category category, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
        LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.category, parent, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.section_image);
        TextView textView = (TextView) view.findViewById(R.id.section_text);        
        imageView.setImageBitmap(category.getImage());
        textView.setText(category.getName());
        return view;
    }    
    
    private CategorySection getSection(Category category) {
        CategorySection result = CATEGORY_TO_SECTION.get(category.getName());
        if(result != null) {
            return result;
        }
        return CategorySection.GENERAL;
    }
    
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int SECTION_VIEW_TYPE = 1;
    private static final int CATEGORY_VIEW_TYPE = 2;
    
    private static final Map<String, CategorySection> CATEGORY_TO_SECTION = 
            new HashMap<String, CategorySection>();
    static {        
        CATEGORY_TO_SECTION.put("Help & Support", CategorySection.HELP);
        CATEGORY_TO_SECTION.put("Advertise with Us", CategorySection.HELP);
        CATEGORY_TO_SECTION.put(ABOUT_US, CategorySection.HELP);
        CATEGORY_TO_SECTION.put("Contact Us", CategorySection.HELP);
    }

    private static enum CategorySection {
        GENERAL ("Sections"),
        HELP ("");        
        
        private final String name;
        
        private CategorySection(String name) {            
            this.name = name;
        }        
        
        String getName() { return name; }
    }

}
