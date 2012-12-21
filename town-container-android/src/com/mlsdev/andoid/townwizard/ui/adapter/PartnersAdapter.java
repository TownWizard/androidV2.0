package com.mlsdev.andoid.townwizard.ui.adapter;

import com.mlsdev.android.townwizard.R;
import com.mlsdev.android.townwizard.model.Partner;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PartnersAdapter extends ArrayAdapter<Partner> {
	private LayoutInflater mLayoutInflater;

	public PartnersAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void clearSearchList() {
		clear();
		notifyDataSetChanged();
	}

	public void removeItem(int position) {
		remove(getItem(position));
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Partner name = getItem(position);
		
		TextView tv;
		convertView = mLayoutInflater.inflate(R.layout.list_item_partners, null);
		tv = (TextView) convertView.findViewById(R.id.name);

		if (name.getName().equals("Load more")) {
			tv.setGravity(Gravity.CENTER);
		} else {
			tv.setGravity(Gravity.LEFT);
		}
		tv.setText(name.getName());

		return convertView;
	}

	public void addItem(Partner item) {
		add(item);
		notifyDataSetChanged();
	}

}
