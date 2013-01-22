package com.townwizard.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.townwizard.android.R;
import com.townwizard.android.model.Category;

public class CategoriesAdapter extends BaseAdapter {

	private List<Category> categories;
	private Context context;

	public CategoriesAdapter(Context context) {
	    this.context = context;
	    categories = new ArrayList<Category>();		
	}
	
	public void addItem(Category item){
		categories.add(item);
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int index) {
		return categories.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View list;
		if (convertView == null) {
		    list = new View(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			list = inflater.inflate(R.layout.section_list_item, parent, false);
		} else {
		    list = convertView;
		}
		
		ImageView imageView = (ImageView) list.findViewById(R.id.section_image);
		TextView textView = (TextView) list.findViewById(R.id.section_text);
		Category category = categories.get(position);
		imageView.setImageBitmap(category.getImage());
		textView.setText(category.getName());
		return list;
	}
	
}