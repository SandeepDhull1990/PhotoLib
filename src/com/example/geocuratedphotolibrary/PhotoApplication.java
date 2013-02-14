package com.example.geocuratedphotolibrary;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.app.Application;

public class PhotoApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc()
				.displayer(new FadeInBitmapDisplayer(600))
				.imageScaleType(ImageScaleType.EXACTLY).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).offOutOfMemoryHandling()
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache()).build();
		ImageLoader.getInstance().init(config);
	}

}
