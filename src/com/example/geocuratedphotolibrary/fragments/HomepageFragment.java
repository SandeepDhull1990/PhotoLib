package com.example.geocuratedphotolibrary.fragments;

import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geocuratedphotolibrary.R;
import com.example.geocuratedphotolibrary.model.Photo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class HomepageFragment extends Fragment {

	private Photo mPhoto;

	private TextView mDescritionTextView;
	private TextView mUserNameTextView;
	private TextView mRelativeTimeTextView;
	private TextView mAddressTextView;
	private ImageView mImageView;
	private ImageButton mUserImageButton;
	private View mLoadingView;
	private OnUserSelectListener mOnUserSelectListener;

	public HomepageFragment() {
	}

	public HomepageFragment(Photo photo) {
		this.mPhoto = photo;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getActivity() instanceof OnUserSelectListener) {
			mOnUserSelectListener = (OnUserSelectListener) getActivity();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_home_page, container, false);
		mDescritionTextView = (TextView) v
				.findViewById(R.id.fragment_description_textView);
		mUserNameTextView = (TextView) v
				.findViewById(R.id.fragment_username_textView);
		mRelativeTimeTextView = (TextView) v
				.findViewById(R.id.frament_relative_time_textView);
		mAddressTextView = (TextView) v.findViewById(R.id.fragment_imageAddress);
		mImageView = (ImageView) v.findViewById(R.id.fragment_imageView);
		mLoadingView = v.findViewById(R.id.fragment_loading_status);
		mUserImageButton = (ImageButton) v.findViewById(R.id.fragment_user_image);
		mUserImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnUserSelectListener != null) {
					mOnUserSelectListener.onUserSelected(mPhoto.getUserID(),mPhoto.getUserName());
				}
			}
		});
		
		mImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				
			}
		});
		
		mLoadingView.setVisibility(View.VISIBLE);
		mAddressTextView.setText(mPhoto.getAddress());
		mDescritionTextView.setText(mPhoto.getImageDescription());
		mUserNameTextView.setText(mPhoto.getUserName());

		// Parsing date
		Date currentDate = new Date();
		String relativeTime = (String) DateUtils.getRelativeTimeSpanString(
				mPhoto.getDateUploaded().getTime(), currentDate.getTime(),
				DateUtils.SECOND_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE);
		mRelativeTimeTextView.setText(relativeTime);

		ImageLoader loader = ImageLoader.getInstance();
		
		loader.displayImage(mPhoto.getUrl(), mImageView, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				mLoadingView.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		});
		return v;
	}
	
	public interface OnUserSelectListener {
		public void onUserSelected(String userId,String userName);
	}
	
}
