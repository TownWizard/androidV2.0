package com.townwizard.android.ui.adapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.townwizard.android.R;
import com.townwizard.android.FacebookPlaceActivity;
import com.townwizard.android.facebook.FacebookPlace;
import com.townwizard.android.utils.ServerConnector;

public class FacebookPlacesAdapter extends ArrayAdapter<FacebookPlace> {

    private Context mContext;
    private List<FacebookPlace> mItems;

    public FacebookPlacesAdapter(Context context, List<FacebookPlace> items) {
        super(context, R.layout.place, items);
        mContext = context;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder = null;
        FacebookPlace item = mItems.get(position);

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(R.layout.place, null);
            holder = new ViewHolder();
            holder.tvPlaceName = (TextView) rowView.findViewById(R.id.tv_place_name);
            holder.tvPlaceCategory = (TextView) rowView.findViewById(R.id.tv_place_category);
            holder.tvPlaceAddress = (TextView) rowView.findViewById(R.id.tv_place_address);
            holder.tvAllCheckins = (TextView) rowView.findViewById(R.id.tv_all_checkins);
            holder.tvFriendsCheckins = (TextView) rowView.findViewById(R.id.tv_friends_checkins);
            holder.ivPlacesImage = (ImageView) rowView.findViewById(R.id.iv_place_picture);
            holder.pbDownloadImage = (ProgressBar) rowView.findViewById(R.id.pb_place_picture);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.tvPlaceName.setText(item.getName());
        holder.tvPlaceCategory.setText(item.getCategory());
        holder.tvPlaceAddress.setText(item.getStreet());

         /*
        if ((item.getCheckins() == null) || (item.getFriendsCheckins() == null)) {
            holder.pbDownloadImage.setVisibility(View.VISIBLE);
            new GetPlacesInfo(holder, item).execute(item.getId());
        } else {
            if ((item.getAllCheckins() != null) && (item.getFriendsCheckins() != null) && (item.getImage() != null)) {
                holder.pbDownloadImage.setVisibility(View.INVISIBLE);
                holder.ivPlacesImage.setImageBitmap(item.getImage());
                holder.tvAllCheckins.setText(item.getAllCheckins() + " total");
                holder.tvFriendsCheckins.setText(item.getFriendsCheckins() + " by friends");
            }
        }
        */
        return rowView;
    }

    private class ViewHolder {
        public TextView tvPlaceName;
        public TextView tvPlaceCategory;
        public TextView tvPlaceAddress;
        public TextView tvAllCheckins;
        public TextView tvFriendsCheckins;
        public ProgressBar pbDownloadImage;
        public ImageView ivPlacesImage;
    }

    private class GetPlacesInfo extends AsyncTask<String, String, Void> {
        private String mPictureURL;
        private String mFriendsCheckins;
        private FacebookPlace mItem;

        public GetPlacesInfo(ViewHolder vh, FacebookPlace item) {
            mFriendsCheckins = "";
            mItem = item;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            /*
            if (values[0].equals("")) {
                mItem.setAllCheckins("0");
            } else {
                mItem.setAllCheckins(values[0]);
            }
            if (values[1].equals("")) {
                mItem.setFriendsCheckins("0");
            } else {
                mItem.setFriendsCheckins(values[1]);
            }
            */
        }

        @Override
        protected Void doInBackground(String... params) {
            String id = params[0];
            try {
                URL url = new URL("https://graph.facebook.com/" + id);

                String response = ServerConnector.getServerResponse(url);

                Log.d("response", response);
                if (response.length() > 0) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        mPictureURL = jsonObject.getString("picture");
                        /*
                        if (mItem.getImage() == null) {
                            mItem.setImage(getPlacePicture(mPictureURL));
                            Log.d("mPictureURL", mPictureURL);
                        }
                        */
                        String returnedParams[] = new String[2];
                        if (jsonObject.has("checkins")) {
                            returnedParams[0] = jsonObject.getString("checkins");
                        } else {
                            returnedParams[0] = "";
                        }
                        // getFriendsCheckinsInfo(id);
                        String resp = null;
                        try {
                            resp = FacebookPlaceActivity.sFb.request(id + "/checkins");
                            Log.d("friends checkins", resp);
                            getCountFriendsCheckins(resp);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        returnedParams[1] = mFriendsCheckins;
                        publishProgress(returnedParams);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            return null;
        }

        private Bitmap getPlacePicture(String imageUrl) {
            Bitmap bitmap = null;
            URL url = null;
            try {
                url = new URL(imageUrl);
                Log.d("IMAGE_URL", url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection httpConnection;
            if(url != null) {
                try {
                    httpConnection = (HttpURLConnection) url.openConnection();
                    InputStream is = httpConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);                    
                    is.close();
                    httpConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        private void getCountFriendsCheckins(String response) {
            JSONObject js;
            try {
                js = new JSONObject(response);
                JSONArray jsArr = js.getJSONArray("data");
                mFriendsCheckins = Integer.toString(jsArr.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            notifyDataSetChanged();
        }

    }
}
