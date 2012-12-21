package com.mlsdev.andoid.townwizard.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlsdev.android.townwizard.R;
import com.mlsdev.android.townwizard.model.FacebookFriend;
import com.mlsdev.android.townwizard.utils.DownloadImageHelper;

public class FacebookFriendsAdapter extends ArrayAdapter<FacebookFriend> implements Filterable {

    private Context mContext;
    private List<FacebookFriend> mItems;
    private List<FacebookFriend> mFindItems;

    public FacebookFriendsAdapter(Context context, int textViewResourceId) {
	super(context, textViewResourceId);
	mContext = context;
    }

    public FacebookFriendsAdapter(Context context, List<FacebookFriend> items) {
	super(context, R.layout.facebook_friends_item, items);
	mContext = context;
	mItems = items;
	mFindItems = items;
    }
    
    
    public void returnFriendList(){
	mItems = mFindItems;
    }

    @Override
    public int getCount() {
	return mItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	View rowView = convertView;
	    FacebookFriend item = mItems.get(position);
	    ViewHolder holder = null;
	    if (rowView == null) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		rowView = inflater.inflate(R.layout.facebook_friends_item, null);
		holder = new ViewHolder();
		holder.tvName = (TextView) rowView.findViewById(R.id.tv_friend_name);
		holder.ivPicture = (ImageView) rowView.findViewById(R.id.iv_friend_image);
		holder.checkBox = (CheckBox) rowView.findViewById(R.id.cb_check_friend);
		rowView.setTag(holder);
	    } else {

		holder = (ViewHolder) rowView.getTag();
	    }
	    holder.tvName.setText(item.getName());
	    if (item.getPictures() != null) {
		holder.ivPicture.setImageBitmap(item.getPictures());
	    } else {
		new AvatarDownloader(item).execute("http://graph.facebook.com/" + item.getId() + "/picture");
	    }
	    
	    if (item.isSelected()){
		holder.checkBox.setVisibility(View.VISIBLE);
		
	    }else{
		holder.checkBox.setVisibility(View.INVISIBLE);
	    }
	return rowView;
    }

    @Override
    public Filter getFilter() {

	return new Filter() {
	    @SuppressWarnings("unchecked")
	    @Override
	    protected void publishResults(CharSequence constraint, FilterResults results) {
		mItems = (List<FacebookFriend>) results.values;
		notifyDataSetChanged();
	    }

	    @Override
	    protected FilterResults performFiltering(CharSequence constraint) {
		String find = constraint.toString().toLowerCase();
		List<FacebookFriend> filteredResults = new ArrayList<FacebookFriend>();

		for (int i = 0; i < getCount(); i++) {
		    FacebookFriend ff = getItem(i);
		    if (ff.getName().toLowerCase().indexOf(find)!=-1) {
			filteredResults.add(ff);
		    }
		}

		FilterResults newFilterResults = new FilterResults();
		newFilterResults.count = filteredResults.size();
		newFilterResults.values = filteredResults;

		return newFilterResults;
	    }
	};

    }

    private class ViewHolder {
	public TextView tvName;
	public ImageView ivPicture;
	public CheckBox checkBox;
    }

    private class AvatarDownloader extends AsyncTask<String, Void, Void> {

	private FacebookFriend mItem;
	private Bitmap mBitmap = null;

	public AvatarDownloader(FacebookFriend item) {
	    mItem = item;
	}

	@Override
	protected Void doInBackground(String... params) {
	    try {
		mBitmap = new DownloadImageHelper().execute(params[0]).get();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (ExecutionException e) {
		e.printStackTrace();
	    }
	    return null;
	}

	@Override
	protected void onPostExecute(Void result) {
	    super.onPostExecute(result);
	    if (mBitmap != null) {
		mItem.setPictures(mBitmap);
		notifyDataSetChanged();
	    }
	}

    }

}
