package com.townwizard.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.townwizard.android.R;

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
    
    
    public static void setScaledToScreenBitmap(ImageView v, Bitmap image) {
        Activity a = (Activity)v.getContext();
        Display d = a.getWindowManager().getDefaultDisplay();
        double displayWidth = d.getWidth();
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double imageRatio = imageWidth / imageHeight;
        double scaledViewHeight = displayWidth / imageRatio;
        Bitmap bm = Bitmap.createScaledBitmap(image, (int)displayWidth, (int)scaledViewHeight, true);
        v.setImageBitmap(bm);
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
    
    public static void hideScreenKeyboard(View input, Context activity) {        
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static void serialize(Object o, File f) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(o);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {out.close();} catch(Exception e) {e.printStackTrace();}
            }
        }
    }
    
    public static Object deserialize(File f) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(f));
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {in.close();} catch(Exception e) {e.printStackTrace();}
            }
        }
        return null;
    }
    
    public static boolean checkConnectivity(final Activity activity) {
        boolean online = isOnline(activity);
        if(!online) {
            Resources res = activity.getResources();
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(activity);
            myAlertDialog.setTitle(res.getString(R.string.no_connection));
            myAlertDialog.setMessage(res.getString(R.string.please_connect));
            myAlertDialog.setNegativeButton(res.getString(R.string.open_settings),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            activity.startActivityForResult(
                                    new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                        }
                    });
            myAlertDialog.setPositiveButton(res.getString(R.string.cancel), 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.cancel();
                        }
                    });
            myAlertDialog.show();
        }
        return online;
    }
    
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    
    public static void setLocale(Locale locale, Context context) {
        Resources r = context.getResources();
        Configuration c = r.getConfiguration();
        c.locale = locale;
        r.updateConfiguration(c, r.getDisplayMetrics());
    }

}
