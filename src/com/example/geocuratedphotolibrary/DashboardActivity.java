package com.example.geocuratedphotolibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.appacitive.android.callbacks.AppacitiveFetchCallback;
import com.appacitive.android.model.AppacitiveConnection;
import com.appacitive.android.model.AppacitiveDistanceMetrics;
import com.appacitive.android.model.AppacitiveError;
import com.appacitive.android.model.AppacitiveObject;
import com.appacitive.android.model.AppacitiveQuery;
import com.appacitive.android.model.AppacitiveUser;
import com.example.geocuratedphotolibrary.fragments.HomepageFragment.OnUserSelectListener;
import com.example.geocuratedphotolibrary.model.Photo;
import com.example.geocuratedphotolibrary.utils.AppacitiveObjectParser;
import com.example.geocuratedphotolibrary.utils.HomePageAdapter;

public class DashboardActivity extends FragmentActivity implements
		LocationListener,OnUserSelectListener {

	private final int CAPTURE_CAMERA = 1000;
	private final int CAPTURE_GALLARY = 1001;
	private final int UPLOAD_PIC = 1002;

	private final int MODE_EXPLORE = 1003;
	private final int MODE_MY_PICS = 1004;
	private final int MODE_USER = 1005;

	private int mCurrentMode ;

	private ArrayList<Photo> mPhotos;
	private HomePageAdapter mAdapter;

	// Views
	private ViewPager mViewPager;

	private LocationManager mLocationManager;
	private String mLocationProvider;
	private Location mLocation;
	private ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		mLocationProvider = mLocationManager.getBestProvider(criteria, false);
		mLocation = mLocationManager.getLastKnownLocation(mLocationProvider);

		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			mActionBar = getActionBar();
			mActionBar.setTitle("Explore");
		}
		
		if (mLocation != null) {
			String query = AppacitiveQuery.queryStringForGeocodeProperty(
					"geolocation", mLocation.getLatitude(),
					mLocation.getLongitude(), 20,
					AppacitiveDistanceMetrics.KILOMETERS);
			query = "query=" + query;
			AppacitiveObject.searchObjects("photo", query, mFetchNearByPics);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentMode = MODE_EXPLORE;
		mLocationManager.requestLocationUpdates(mLocationProvider, 600000,
				4000, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dashboard_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionExplore:
			if (mCurrentMode != MODE_EXPLORE) {
				if (mLocation != null) {
					mCurrentMode = MODE_EXPLORE;
					if(mActionBar != null) {
						mActionBar.setTitle("Explore");
					}
					String query = AppacitiveQuery.queryStringForGeocodeProperty(
							"geolocation", mLocation.getLatitude(),
							mLocation.getLongitude(), 20,
							AppacitiveDistanceMetrics.KILOMETERS);
					query = "query=" + query;
					setProgressBarIndeterminateVisibility(true);
					AppacitiveObject
					.searchObjects("photo", query, mFetchNearByPics);
				} else {
					Toast.makeText(getBaseContext(),
							"Current location unavailable", Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
		case R.id.actionCamera:
			Intent camerIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(camerIntent, CAPTURE_CAMERA);
			break;
		case R.id.actionMyImages:
			if (mCurrentMode != MODE_MY_PICS) {
				mCurrentMode = MODE_MY_PICS;
				if(mActionBar != null) {
					mActionBar.setTitle("My Images");
				}
				setProgressBarIndeterminateVisibility(true);
				AppacitiveConnection.searchForConnectedArticles("user_photo",
						AppacitiveUser.currentUser.getObjectId(), mFetchUserPics);
			}
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * onClickListener for capturing an image from Camera/gallery.
	 * 
	 * @param view
	 *            on which click event has occurred.
	 */
	public void onClickCaptureImage(View view) {
		// openImageIntent();
		Intent camerIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(camerIntent, CAPTURE_CAMERA);
	}

	/**
	 * onClickListener for explore button.
	 * 
	 * @param view
	 *            on which click event has occurred.
	 */
	public void onClickExploreImage(View view) {
		// final Intent intent = new Intent(this,
		// PinLocationOnMapViewActivity.class);
		// startActivity(intent);
	}

	/**
	 * Creates a chooser to select an image. This chooser will search for all
	 * the apps in the device that can retrieve an image from either camera or
	 * gallery.
	 */
	private void openImageIntent() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select option");
		final String[] items = new String[] { "Image from camera",
				"From Gallery" };
		final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		builder.setAdapter(adapter1, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					// Start camera
					Intent camerIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(camerIntent, CAPTURE_CAMERA);
				}
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bitmap bmp = null;
			if (requestCode == CAPTURE_CAMERA) {
				bmp = (Bitmap) data.getExtras().get("data");
			} else if (requestCode == CAPTURE_GALLARY) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				bmp = BitmapFactory.decodeFile(picturePath);
			} else if (requestCode == UPLOAD_PIC) {
				String query = AppacitiveQuery.queryStringForGeocodeProperty(
						"geolocation", mLocation.getLatitude(),
						mLocation.getLongitude(), 20,
						AppacitiveDistanceMetrics.KILOMETERS);
				query = "query=" + query;
				setProgressBarIndeterminateVisibility(true);
				AppacitiveObject.searchObjects("photo", query, mFetchNearByPics);
			}
			if (bmp != null) {
				Intent intent = new Intent(this, PhotoDetailActivity.class);
				intent.putExtra("bmp", bmp);
				intent.putExtra("location", mLocation);
				startActivityForResult(intent, UPLOAD_PIC);
			}
		}
	}

	private AppacitiveFetchCallback mFetchUserPics = new AppacitiveFetchCallback() {
		// Appacitive Callback Methods
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(Map<String, Object> response) {

			mPhotos = new ArrayList<Photo>();
			List<Map> connections = (List<Map>) response.get("connections");
			if (mPhotos == null) {
				mPhotos = new ArrayList<Photo>();
			} else {
				mPhotos.clear();
			}

			for (Map<String, Object> connection : connections) {
				Map<String, Object> endpointMap = (Map<String, Object>) connection
						.get("__endpointb");
				Map<String, Object> photoMap = (Map<String, Object>) endpointMap
						.get("article");
				Photo photo = AppacitiveObjectParser.parsePhoto(photoMap);
				mPhotos.add(photo);
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAdapter = new HomePageAdapter(getSupportFragmentManager(),
							mPhotos);
					setProgressBarIndeterminateVisibility(false);
					mViewPager.setAdapter(mAdapter);
				}
			});

		}

		@Override
		public void onFailure(AppacitiveError error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getBaseContext(), "Error",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	private AppacitiveFetchCallback mFetchNearByPics = new AppacitiveFetchCallback() {

		@Override
		public void onSuccess(Map<String, Object> response) {
			Log.d("TAG", "Reached Here  " + response.toString());
			List<Map> articleList = (List<Map>) response.get("articles");
			if (mPhotos == null) {
				mPhotos = new ArrayList<Photo>();
			} else {
				mPhotos.clear();
			}
			for (Map<String, Object> photoMap : articleList) {
				Photo photo = AppacitiveObjectParser.parsePhoto(photoMap);
				mPhotos.add(photo);
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAdapter = new HomePageAdapter(getSupportFragmentManager(),
							mPhotos);
					setProgressBarIndeterminateVisibility(false);
					mViewPager.setAdapter(mAdapter);
				}
			});
		}

		@Override
		public void onFailure(AppacitiveError error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getBaseContext(), "Error",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	// Location listener
	@Override
	public void onLocationChanged(Location location) {
		if (this.mLocation == null) {
			this.mLocation = location;
			String query = AppacitiveQuery.queryStringForGeocodeProperty(
					"geolocation", mLocation.getLatitude(),
					mLocation.getLongitude(), 20,
					AppacitiveDistanceMetrics.KILOMETERS);
			query = "query=" + query;
			setProgressBarIndeterminateVisibility(true);
			AppacitiveObject.searchObjects("photo", query, mFetchNearByPics);
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	
//	OnUserSelectedListener
	@Override
	public void onUserSelected(String userId,String userName) {
		Long id = Long.parseLong(userId);
		setProgressBarIndeterminateVisibility(true);
		if(mActionBar != null) {
			mActionBar.setTitle(userName + "'s images");
		}
		this.mCurrentMode = MODE_USER;
		AppacitiveConnection.searchForConnectedArticles("user_photo",
				id, mFetchUserPics);
	}

}
