package com.townwizard.android.partner;

import com.townwizard.android.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PartnersAdapter extends ArrayAdapter<Partner> {
	
    private LayoutInflater layoutInflater;

    public PartnersAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		layoutInflater = LayoutInflater.from(context);
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
	    View view = layoutInflater.inflate(R.layout.partner, null);
	    
	    Partner partner = getItem(position);
	    TextView tv = (TextView) view.findViewById(R.id.name);

		if (partner.getName().equals("Load more")) {
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.BLACK);
			View circle = view.findViewById(R.id.circle);
			circle.setVisibility(View.INVISIBLE);
		} else {
			tv.setGravity(Gravity.LEFT);
		}
		
		tv.setText(partner.getName());
		return view;
	}

	public void addPartner(Partner item) {
		add(item);
		notifyDataSetChanged();
	}
}