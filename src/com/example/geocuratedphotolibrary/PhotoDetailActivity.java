package com.example.geocuratedphotolibrary;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appacitive.android.callbacks.AppacitiveCallback;
import com.appacitive.android.callbacks.AppacitiveUploadCallback;
import com.appacitive.android.model.AppacitiveConnection;
import com.appacitive.android.model.AppacitiveError;
import com.appacitive.android.model.AppacitiveFile;
import com.appacitive.android.model.AppacitiveGeopoint;
import com.appacitive.android.model.AppacitiveObject;
import com.appacitive.android.model.AppacitiveUser;
import com.appacitive.android.util.AppacitiveRequestMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PhotoDetailActivity extends Activity {

	private ImageView mPhotoView;
	private EditText mDescriptionEditText;
	private TextView mAddressTextView;
	private Bitmap mImageBitmap;

	private String mAddress = "Resolving Location...";
	private AppacitiveGeopoint currentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_detail);
		mPhotoView = (ImageView) findViewById(R.id.photoImageView);
		mDescriptionEditText = (EditText) findViewById(R.id.photoDescriptionEditText);
		mAddressTextView = (TextView) findViewById(R.id.photoAddressTextView);

		Intent startingIntent = getIntent();

		Location location = startingIntent.getParcelableExtra("location");
		if (location != null) {
			currentLocation = new AppacitiveGeopoint();
			currentLocation.latitude = location.getLatitude();
			currentLocation.longitude = location.getLongitude();
			new LocationAddressFetcher(true).execute();
		} else {
			new LocationAddressFetcher(false).execute();
		}

		mImageBitmap = startingIntent.getParcelableExtra("bmp");
		
		mPhotoView.setImageBitmap(mImageBitmap);
		mAddressTextView.setText(mAddress);
	}

	public void uploadImage(View v) {
		v.setEnabled(false);
		final String description = mDescriptionEditText.getText().toString();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		final ProgressDialog progress = ProgressDialog.show(this,
				"Please Wait..", "Uploading Image ..", true);

		final String title = Long.toHexString(Double.doubleToLongBits(Math
				.random()));

		AppacitiveFile.uploadData(title, "image/png", 5, byteArray,
				new AppacitiveUploadCallback() {

					@Override
					public void onSuccess(String url) {
						final AppacitiveObject object = new AppacitiveObject("photo");
						object.addProperty("imagekey", title);
						object.addProperty("description", description);
						object.addProperty("userid",AppacitiveUser.currentUser.getObjectId());
						String userName = (String) AppacitiveUser.currentUser.getProperty("firstname");
						object.addProperty("username", userName);
						object.addProperty("url", url);
						object.addProperty("address", mAddress);

						if (currentLocation != null) {
							object.addProperty("geolocation",
									currentLocation.toString());
						}

						object.saveObject(new AppacitiveCallback() {

							@Override
							public void onSuccess() {

								AppacitiveConnection connection = new AppacitiveConnection("user_photo");
								connection.setArticleAId(AppacitiveUser.currentUser.getObjectId());
								connection.setArticleBId(object.getObjectId());
								connection.setLabelA("user");
								connection.setLabelB("photo");
								connection.createConnection(new AppacitiveCallback() {

											@Override
											public void onSuccess() {
												// Moving Back to the dash board
												// activity
												runOnUiThread(new Runnable() {

													@Override
													public void run() {
														progress.dismiss();
														setResult(RESULT_OK);
														finish();
													}
												});
											}

											@Override
											public void onFailure(
													AppacitiveError error) {
											}
										});
							}

							@Override
							public void onFailure(final AppacitiveError error) {
								Log.d("TAG", "Reached Here in onFailure "
										+ error.toString());
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										progress.setMessage(error.getMessage());
										progress.dismiss();
									}
								});
							}
						});

					}

					@Override
					public void onFailure(final AppacitiveError error) {
						Log.d("TAG",
								"Reached Here in onFailure " + error.toString());
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								progress.setMessage(error.getMessage());
								progress.dismiss();
							}
						});
					}
				});
	}

	public void onCancelClick(View v) {
		finish();
	}

	class LocationAddressFetcher extends AsyncTask<String, Void, String> {

		private boolean isLocationCorrdinatesKnown;

		public LocationAddressFetcher(boolean locationCoordinateKnowsn) {
			isLocationCorrdinatesKnown = locationCoordinateKnowsn;
		}

		@Override
		protected String doInBackground(String... params) {
			if (!isLocationCorrdinatesKnown) {

				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				List<String> matchingProviders = locationManager
						.getAllProviders();
				Location currentLocation = null;
				for (String provider : matchingProviders) {
					currentLocation = locationManager
							.getLastKnownLocation(provider);
				}

				if (currentLocation == null) {
					return "Unknown";
				} else {
					PhotoDetailActivity.this.currentLocation = new AppacitiveGeopoint();
					PhotoDetailActivity.this.currentLocation.latitude = currentLocation
							.getLatitude();
					PhotoDetailActivity.this.currentLocation.longitude = currentLocation
							.getLongitude();
				}
			}

			URL url;

			try {
				url = new URL(
						"http://maps.googleapis.com/maps/api/geocode/json?latlng="
								+ PhotoDetailActivity.this.currentLocation.latitude
								+ ","
								+ PhotoDetailActivity.this.currentLocation.longitude
								+ "&sensor=true");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod(AppacitiveRequestMethods.GET
						.requestMethod());
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Unknown";
				}
				InputStream is = connection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(is));
				StringBuffer buffer = new StringBuffer();
				String response;
				while ((response = bufferedReader.readLine()) != null) {
					buffer.append(response);
				}
				Gson gson = new Gson();
				Type typeOfClass = new TypeToken<Map<String, Object>>() {
				}.getType();
				Map<String, Object> responseMap = gson.fromJson(
						buffer.toString(), typeOfClass);
				List<Map> results = (List<Map>) responseMap.get("results");
				if (results.size() > 0) {
					Map<String, Object> location = results.get(0);
					List<Map> addressMapComponents = (List<Map>) location.get("address_components");
					StringBuffer address = null;
					for(Map<String,Object> component : addressMapComponents) {
						List<String> types = (List<String>) component.get("types");
						boolean inculdeThisComponent = false;
						for(String key : types) {
							if(key.equals("route")) {
								inculdeThisComponent = true;
							} else if(key.equals("sublocality")) {
								inculdeThisComponent = true;
							} else if(key.equals("locality")) {
								inculdeThisComponent = true;
							} else if(key.equals("country")) {
								inculdeThisComponent = true;
							}
						}
						if(inculdeThisComponent) {
							if(address == null) {
								address = new StringBuffer();
								address.append(component.get("short_name"));
							} else {
								address.append(", " + component.get("short_name"));
							}
						}
					}
					return address.toString();
//					if (location.containsKey("formatted_address")) {
//						return (String) location.get("formatted_address");
//					}
				}
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "Unknown";
		}

		@Override
		protected void onPostExecute(String result) {
			mAddress = result;
			mAddressTextView.setText(result);
		}

	}

}
