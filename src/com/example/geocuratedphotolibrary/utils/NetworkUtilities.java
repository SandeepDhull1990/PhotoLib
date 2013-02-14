package com.example.geocuratedphotolibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtilities {

	/**
	 * Here if condition check for WiFi and Mobile network is available or not.
	 * If anyone of them is available or connected then it will return true,
	 * otherwise false.
	 * 
	 * @param context
	 *            the Context of an application.
	 * @return true if device is connected to Internet, false otherwise.
	 */
	public static boolean checkInternetConnectivity(Context context) {
		final ConnectivityManager conn_manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo network_info = conn_manager.getActiveNetworkInfo();

		if (network_info != null && network_info.isConnected()) {
			if (network_info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
			else if (network_info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}
}
