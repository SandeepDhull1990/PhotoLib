package com.example.geocuratedphotolibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import android.util.Log;

import com.example.geocuratedphotolibrary.model.Photo;

public class AppacitiveObjectParser {

	public static Photo parsePhoto(Map<String,Object> photoMap) {
		Photo photo = new Photo();
		photo.setObjectId((String) photoMap.get("__id"));
		photo.setImageKey((String) photoMap.get("imagekey"));
		photo.setImageDescription((String) photoMap.get("description"));
		photo.setUrl((String) photoMap.get("url"));
		photo.setAddress((String) photoMap.get("address"));
		photo.setUserName((String) photoMap.get("username"));
		photo.setUserID((String) photoMap.get("userid"));
		String geocode = (String) photoMap.get("geolocation");
		StringTokenizer tokenizer = new StringTokenizer(geocode, ",");
		double latitude = Double.parseDouble(tokenizer.nextToken());
		double longitude = Double.parseDouble(tokenizer.nextToken());
		photo.setLatitude(latitude);
		photo.setLongitude(longitude);

		String dateString = (String) photoMap.get("__utcdatecreated");
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date date = formatter.parse(dateString);
			Log.d("TAG", "Date is " + date.toString());
			photo.setDateUploaded(date);
		} catch (ParseException e) {
			Log.d("TAG", "Exeption in date parsing" + e);
			e.printStackTrace();
		}
		return photo;
	}
	
}
