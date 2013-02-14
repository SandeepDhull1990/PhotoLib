package com.example.geocuratedphotolibrary.utils;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.geocuratedphotolibrary.fragments.HomepageFragment;
import com.example.geocuratedphotolibrary.model.Photo;

public class HomePageAdapter extends FragmentStatePagerAdapter {
	
	private ArrayList<Photo> mPhotoList;
	
	public HomePageAdapter(FragmentManager fm, ArrayList<Photo> photoList) {
		super(fm);
		mPhotoList = photoList;
	}

	@Override
	public Fragment getItem(int position) {
		HomepageFragment fragment = new HomepageFragment(mPhotoList.get(position));
		return fragment;
	}

	@Override
	public int getCount() {
		return mPhotoList.size();
	}

}
