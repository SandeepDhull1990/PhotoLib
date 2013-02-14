package com.example.geocuratedphotolibrary.model;

import java.util.Date;

/**
 * A class to store the images, description, key , latitude and longitude for a
 * User.
 * 
 * @author One-Associates Pvt. Ltd.
 * 
 */
public class Photo {

	@Override
	public String toString() {
		return "Photo [mImageDescription=" + mImageDescription + ", mImageKey="
				+ mImageKey + ", mImageTitleText=" + mImageTitleText
				+ ", mLatitude=" + mLatitude + ", mLongitude=" + mLongitude
				+ "]";
	}

	private String mImageDescription, mImageKey, mImageTitleText, mUrl,mAddress,mUserName,mUserID;
	private Date mDateUploaded;
	private String mObjectId;
	
	private double mLatitude, mLongitude;

	public String getImageTitleText() {
		return mImageTitleText;
	}

	public void setImageTitleText(String mImageTitleText) {
		this.mImageTitleText = mImageTitleText;
	}

	public String getImageDescription() {
		return mImageDescription;
	}

	public void setImageDescription(String mImageDescription) {
		this.mImageDescription = mImageDescription;
	}

	public String getImageKey() {
		return mImageKey;
	}

	public void setImageKey(String mImageKey) {
		this.mImageKey = mImageKey;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public Date getDateUploaded() {
		return mDateUploaded;
	}

	public void setDateUploaded(Date mDateUploaded) {
		this.mDateUploaded = mDateUploaded;
	}

	public String getObjectId() {
		return mObjectId;
	}

	public void setObjectId(String mObjectId) {
		this.mObjectId = mObjectId;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getUserID() {
		return mUserID;
	}

	public void setUserID(String mUserID) {
		this.mUserID = mUserID;
	}
}
