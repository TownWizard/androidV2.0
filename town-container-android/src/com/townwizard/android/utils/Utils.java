package com.townwizard.android.utils;

import java.util.Iterator;
import java.util.List;

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

}
