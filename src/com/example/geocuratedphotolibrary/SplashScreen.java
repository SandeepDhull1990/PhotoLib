package com.example.geocuratedphotolibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.appacitive.android.callbacks.AppacitiveCallback;
import com.appacitive.android.model.Appacitive;
import com.appacitive.android.model.AppacitiveError;
import com.example.geocuratedphotolibrary.utils.Constants;

public class SplashScreen extends Activity implements AppacitiveCallback {

	private static String TAG = SplashScreen.class.getName();
	private static long SLEEP_TIME = 5; // Sleep for some time

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // Removes
																// notification
																// bar

		setContentView(R.layout.activity_splash_screen);

		Appacitive.initializeAppacitive(getBaseContext(), Constants.API_KEY,
				this);

	}

	@Override
	public void onSuccess() {
		Intent intent = new Intent(getBaseContext(), LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void onFailure(final AppacitiveError error) {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreen.this);
		alertDialog.setTitle("Error...");
		alertDialog.setMessage(error.getMessage());
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setNegativeButton("Exit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Appacitive.endSession();
						finish();
						dialog.cancel();
					}
				});
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				alertDialog.show();
			}
		});
	}
}