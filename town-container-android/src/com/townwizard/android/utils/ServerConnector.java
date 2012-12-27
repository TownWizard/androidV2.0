package com.townwizard.android.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnector {

    public static String getServerResponse(URL url) {
	String result = "";
	HttpURLConnection httpconnection;
	try {
	    httpconnection = (HttpURLConnection) url.openConnection();
	    if (httpconnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
		BufferedReader rd = new BufferedReader(new InputStreamReader(httpconnection.getInputStream()));
		StringBuffer sb = new StringBuffer();

		while ((result = rd.readLine()) != null) {
		    sb.append(result);
		}
		rd.close();
		httpconnection.disconnect();
		result = sb.toString();
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}

	return result;
    }
}
