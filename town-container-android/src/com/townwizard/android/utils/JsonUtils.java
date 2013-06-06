package com.townwizard.android.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.townwizard.android.partner.Partner;

public final class JsonUtils {
    
    public static final int STATUS_FOUND = 1;
    
    private JsonUtils() {}

    public static int getStatus(JSONObject mainJson) throws JSONException {
        return mainJson.getInt("status");
    }
    
    public static Partner jsonToPartner(JSONObject mainJson) throws JSONException {
        int status = mainJson.getInt("status");
        if (status == STATUS_FOUND) {
            JSONObject partnerJson = mainJson.getJSONObject("data");
            return partnerJsonToPartner(partnerJson);
        }
        return null;
    }
    
    public static List<Partner> jsonToPartnerList(JSONObject mainJson) throws JSONException {
        int status = mainJson.getInt("status");
        if(status == STATUS_FOUND) {            
            JSONArray jsArr = mainJson.getJSONArray("data");
            List<Partner> result = new ArrayList<Partner>(jsArr.length());
            for (int i = 0; i < jsArr.length(); i++) {
                Partner p = partnerJsonToPartner(jsArr.getJSONObject(i));                
                result.add(p);
            }
            return result;
        }
        return Collections.<Partner>emptyList();
    }
    
    public static int getNextOffset(JSONObject mainJson, int currentOffset) throws JSONException {
        int nextOffset = currentOffset;
        JSONObject metaInf = mainJson.getJSONObject("meta");
        int total = metaInf.getInt("total");
        int limit = metaInf.getInt("limit");
        if (currentOffset + limit < total) {
            nextOffset = metaInf.getInt("next_offset");
        } else {
            nextOffset = 0;
        }
        return nextOffset;
    }    
    
    private static Partner partnerJsonToPartner(JSONObject partnerJson) throws JSONException {
        int id = partnerJson.getInt("id");
        String name = partnerJson.getString("name");                
        String androidAppId = partnerJson.getString("android_app_id");
        String imageUrl = partnerJson.getString("image");
        String siteUrl = partnerJson.getString("website_url");
        if (siteUrl.charAt(siteUrl.length() - 1) != '/') {
            siteUrl += "/";
        }
        String language = partnerJson.optString("language");
        //String language = "es";
        return new Partner(name, siteUrl, androidAppId, id, imageUrl, language);
    }

}
