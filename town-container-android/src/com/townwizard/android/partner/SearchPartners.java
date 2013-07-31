package com.townwizard.android.partner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.townwizard.android.R;
import com.townwizard.android.TownWizardActivity;
import com.townwizard.android.category.Category;
import com.townwizard.android.config.Config;
import com.townwizard.android.config.Constants;
import com.townwizard.android.utils.ServerConnector;

public class SearchPartners extends AsyncTask<String, Partner, Integer> {
    
    private static final String OFFSET = "&offset=";
    private static final int STATUS_FOUND = 1;
    
    private int status = 0;
    private PartnersAdapter partnersAdapter;
    private List<Partner> partners = new ArrayList<Partner>();
    private TownWizardActivity context;

    public SearchPartners(TownWizardActivity context, PartnersAdapter partnersAdapter) {
    	this.partnersAdapter = partnersAdapter;
    	this.context = context;
    }

    @Override
    protected void onProgressUpdate(Partner... values) {
    	super.onProgressUpdate(values);
    	//partnersAdapter.addPartner(values[0]);
    	partners.add(values[0]);
    }

    @Override
    protected Integer doInBackground(String... params) {
        String searchRequest = params[0];
        boolean isTermSearch = searchRequest.contains("q=");
        int offset = Integer.parseInt(params[1]);
        int nextOffset = offset;
        try {
            URL partnerSearchUrl = getPartnerSearchUrl(searchRequest, offset);          
            JSONObject mainJsonObject = getPartnersJson(partnerSearchUrl);
            status = mainJsonObject.getInt("status");
            
            boolean disableMagicWands = (isTermSearch && status == STATUS_FOUND) || (offset > 0);
            if(!disableMagicWands) {
                URL twPartnerSearchUrl = getTownWizardPartnerUrl();
                JSONObject twPartnerJsonObject = getPartnersJson(twPartnerSearchUrl);
                List<Partner> partners = convertToPartnerList(twPartnerJsonObject);
                if(!partners.isEmpty()) {                    
                    Partner p = partners.get(0);
                    List<Category> categories = Category.getCategories(
                            context, Integer.valueOf(p.getId()).toString());                    
                    if(findInList(categories, Constants.EVENTS) != null) {
                        publishProgress(new Partner(Constants.CONTENT_PARTNER_EVENTS,
                                p.getUrl(), p.getAndroidAppId(), p.getId(), p.getImageUrl()));                        
                    }                    
                    if(findInList(categories, Constants.RESTAURANTS) != null) {
                        publishProgress(new Partner(Constants.CONTENT_PARTNER_RESTAURANTS,
                                p.getUrl(), p.getAndroidAppId(), p.getId(), p.getImageUrl()));                        
                    }
                    if(findInList(categories, Constants.PLACES) != null) {
                        publishProgress(new Partner(Constants.CONTENT_PARTNER_PLACES,
                                p.getUrl(), p.getAndroidAppId(), p.getId(), p.getImageUrl()));                        
                    }
                }
            }
            
            if (status == STATUS_FOUND) {
                List<Partner> partners = convertToPartnerList(mainJsonObject);
                for(Partner p : partners) {
                    if(!Config.CONTENT_PARTNER_NAME.equals(p.getName())) {
                        publishProgress(p);
                    }
                }
                nextOffset = getNextOffset(mainJsonObject, offset);
                if(nextOffset != 0) {
                    publishProgress(new Partner("Load more", "", "", -1, ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextOffset;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(status == STATUS_FOUND) {
           partnersAdapter.addPartners(partners); 
        } else {
            if(!partners.isEmpty()) {
                Partner partner = partners.get(0);
                context.startWebActivityWithHome(partner);
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                String title = context.getResources().getString(R.string.whoops);
                String message = context.getResources().getString(R.string.partners_not_found);
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }
    }
    
    private URL getPartnerSearchUrl(String searchRequest, int offset) throws MalformedURLException {
        String url = Config.PARTNER_API + "?" + searchRequest + OFFSET + offset;
        Log.d("Partner search URL = ", url);
        return new URL(url);
    }
    
    private URL getTownWizardPartnerUrl() throws MalformedURLException {
        return getPartnerSearchUrl("q=" + Config.CONTENT_PARTNER_NAME, 0);
    }
    
    private JSONObject getPartnersJson(URL searchUrl) throws JSONException {
        String response = ServerConnector.getServerResponse(searchUrl);
        return new JSONObject(response);
    }
    
    private List<Partner> convertToPartnerList(JSONObject mainJsonObject) throws JSONException {        
        JSONArray jsArr = mainJsonObject.getJSONArray("data");
        List<Partner> result = new ArrayList<Partner>(jsArr.length());
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject jsObject = jsArr.getJSONObject(i);
            int partnerId = jsObject.getInt("id");
            String name = jsObject.getString("name");
            String url = jsObject.getString("website_url");
            String androidAppId = jsObject.getString("android_app_id");
            String imageUrl = jsObject.getString("image");
            if (url.charAt(url.length() - 1) != '/') {
                url += "/";
            }
            result.add(new Partner(name, url, androidAppId, partnerId, imageUrl));
        }
        return result;
    }
    
    private int getNextOffset(JSONObject mainJsonObject, int offset) throws JSONException {
        int nextOffset = offset;
        JSONObject metaInf = mainJsonObject.getJSONObject("meta");
        int total = metaInf.getInt("total");
        int limit = metaInf.getInt("limit");
        if (offset + limit < total) {
            nextOffset = metaInf.getInt("next_offset");
        } else {
            nextOffset = 0;
        }
        return nextOffset;
    }
    
    private Category findInList(List<Category> categories, String name) {
        for(Category c : categories) {
            // bhavan: use display name: if(name.equals(c.getName())) return c;
            if(name.equals(c.getDisplayName())) return c;
        }
        return null;
    }
}
