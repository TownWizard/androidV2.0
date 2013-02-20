package com.townwizard.android.utils;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.view.Display;
import android.widget.TextView;

public final class Utils {
    
    private Utils(){}
    
    public static String join(List<?> list, String separator, String encloser) {        
        if(list == null || list.isEmpty()) return "";
        String sep = separator != null ? separator : ",";
        String enc = encloser != null ? encloser : "";
        
        StringBuilder sb = new StringBuilder();
        Iterator<?> i = list.iterator();
        while(i.hasNext()) {
            sb.append(enc).append(i.next()).append(enc);
            if(i.hasNext()) sb.append(sep);
        }
        return sb.toString();
    }
    
    public static String join(List<?> list) {
        return join(list, null, null);
    }
    
    public static void eclipsize(TextView v, double percentOfScreenWidth, double fontHeightToWidthRatio) {
        Activity a = (Activity)v.getContext();
        Display d = a.getWindowManager().getDefaultDisplay();
        
        int width = (int)(d.getWidth() * percentOfScreenWidth);
        float textSize = v.getTextSize();
        
        int allowedLength = (int)((width / textSize) * fontHeightToWidthRatio);
        
        String text = v.getText().toString();
        if(text.length() > allowedLength) {
            text = text.substring(0, allowedLength) + "...";
            v.setText(text);
        }
    }

}
