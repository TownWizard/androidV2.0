package com.townwizard.android.partner;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.townwizard.android.R;

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
	    Partner partner = getItem(position);
	    View view = partner.isContentPartner() ? 
	            layoutInflater.inflate(R.layout.partner_first, null) :
	            layoutInflater.inflate(R.layout.partner, null);

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
	
	public void addPartners(List<Partner> partners) {
	    for(Partner p : partners) add(p);
	    notifyDataSetChanged();
	}
}