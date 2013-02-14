package com.example.geocuratedphotolibrary.utils;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appacitive.android.callbacks.AppacitiveDownloadCallback;
import com.appacitive.android.model.AppacitiveError;
import com.appacitive.android.model.AppacitiveFile;
import com.example.geocuratedphotolibrary.R;
import com.example.geocuratedphotolibrary.model.Photo;

public class DashboardAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<com.example.geocuratedphotolibrary.model.Photo> mPhotoList;

	private Handler mHandlerToSetImageBitmap;

	public DashboardAdapter(Context context, ArrayList<Photo> imageShareDatas) {
		mPhotoList = new ArrayList<Photo>();
		mHandlerToSetImageBitmap = new Handler();
		mPhotoList.addAll(imageShareDatas);
		mContext = context;
	}

	@Override
	public int getCount() {
		return mPhotoList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mPhotoList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = convertView;
		Photo photo = null;

		final LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view == null) {
			if (position % 2 == 0) {
			view = inflater.inflate(R.layout.image_share_list_item_left,
					null);
			} else {
				view = inflater.inflate(R.layout.image_share_list_item_right,
						null);
			}
		}
		TextView mUserImageTitle = (TextView) view.findViewById(R.id.imageName);
		TextView mUserImageShareText = (TextView) view.findViewById(R.id.shareText);
		final ImageView mUserImage = (ImageView) view.findViewById(R.id.userImage);

		photo = (Photo) getItem(position);
		String key = photo.getImageKey();
		mUserImageTitle.setText(photo.getImageKey());
		mUserImageShareText.setText(photo.getImageDescription());
		
		AppacitiveFile.download(key, new AppacitiveDownloadCallback() {

			@Override
			public void onSuccess(InputStream inputStream) {
				Options option = new Options();
				option.inSampleSize = 2;
				final Bitmap bmp = BitmapFactory.decodeStream(inputStream,null,option);
				mHandlerToSetImageBitmap.post(new Runnable() {

					@Override
					public void run() {
						mUserImage.setImageBitmap(bmp);
					}
				});
			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.d("TAG", "Reached Here in onFailure");
			}
		});
		return view;
	}

}
