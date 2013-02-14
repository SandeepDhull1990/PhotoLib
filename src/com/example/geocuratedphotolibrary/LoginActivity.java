package com.example.geocuratedphotolibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.appacitive.android.callbacks.AppacitiveAuthenticationCallback;
import com.appacitive.android.model.AppacitiveError;
import com.appacitive.android.model.AppacitiveUser;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well as sign in for already registered user.
 */
public class LoginActivity extends Activity implements
		AppacitiveAuthenticationCallback {
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// Handler to ui thread
	private Handler mHandler;

	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		mHandler = new Handler();
		mUserNameView = (EditText) findViewById(R.id.userName);
		mUserNameView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(LoginActivity.this,
								SignUpActivity.class);
						startActivity(intent);
					}
				});

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			session = new Session(this);
		}
		Session.setActiveSession(session);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are errors in the fields entered (invalid email, missing fields,
	 * etc.), the errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		mUserNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError("Password can't be empty");
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 3) {
			mPasswordView.setError("Wrong Password");
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mUserNameView.setError("Username can't be null");
			focusView = mUserNameView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText("Signing in..");
			mLoginStatusView.bringToFront();
			mLoginStatusView.setVisibility(View.VISIBLE);
			String userName = mUserNameView.getText().toString();
			String password = mPasswordView.getText().toString();
			signIn(userName, password);
		}
	}

	private void signIn(String userName, String password) {
		AppacitiveUser.authenticate(userName, password, this);
	}

	public void loginWithFacebook(View v) {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.d("TAG", "Reached Herer " + session.getAccessToken());
			switch (state) {
			case OPENED:
				if (session.getAccessToken() != null) {
					mLoginStatusMessageView.setText("Signing in..");
					mLoginStatusView.bringToFront();
					mLoginStatusView.setVisibility(View.VISIBLE);
					AppacitiveUser.authenticateWithFacebook(
							session.getAccessToken(), LoginActivity.this);
				}
				break;
			case CLOSED_LOGIN_FAILED:

				break;

			default:
				break;
			}
		}
	}

	// Authentication Callbacks
	@Override
	public void onSuccess() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mLoginStatusMessageView.setText("Login Successfull !");
				mLoginStatusView.setVisibility(View.GONE);
				findViewById(R.id.sign_in_button).setEnabled(true);
				findViewById(R.id.register_button).setEnabled(true);
				Intent intent = new Intent(LoginActivity.this,
						DashboardActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onFailure(AppacitiveError error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				findViewById(R.id.sign_in_button).setEnabled(true);
				findViewById(R.id.register_button).setEnabled(true);
				mLoginStatusMessageView.setText("Wrong Username or password !");
			}
		});
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLoginStatusView.setVisibility(View.GONE);
			}
		}, 2000);
	}

}
