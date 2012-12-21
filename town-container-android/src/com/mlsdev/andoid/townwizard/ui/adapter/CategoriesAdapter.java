package com.mlsdev.andoid.townwizard.ui.adapter;

import java.util.ArrayList;

import com.mlsdev.android.townwizard.R;
import com.mlsdev.android.townwizard.model.Categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoriesAdapter extends BaseAdapter {

	private ArrayList<Categories> mCategoriesModel;
	private Context mContext;

	public CategoriesAdapter(Context context) {
		mCategoriesModel = new ArrayList<Categories>();
		mContext = context;
	}
	public void addItem(Categories item){
		mCategoriesModel.add(item);
	}

	@Override
	public int getCount() {
		return mCategoriesModel.size();
	}

	@Override
	public Object getItem(int index) {
		return mCategoriesModel.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Categories item = mCategoriesModel.get(position);
		View grid;

		if (convertView == null) {
			grid = new View(mContext);
			LayoutInflater inflater = LayoutInflater.from(mContext);
			grid = inflater.inflate(R.layout.grid, parent, false);
		} else {
			grid = (View) convertView;
		}

		ImageView imageView = (ImageView) grid.findViewById(R.id.imagepart);
		TextView textView = (TextView) grid.findViewById(R.id.textpart);
		imageView.setImageBitmap(item.getImage());
		textView.setText(item.getName());
		return grid;
	}

}
