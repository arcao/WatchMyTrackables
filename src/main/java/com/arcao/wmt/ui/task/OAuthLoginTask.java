package com.arcao.wmt.ui.task;

import android.content.Intent;
import android.os.AsyncTask;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.UserProfile;
import com.arcao.geocaching.api.exception.GeocachingApiException;
import com.arcao.geocaching.api.exception.NetworkException;
import com.arcao.geocaching.api.util.DeviceInfoFactory;
import com.arcao.wmt.App;
import com.arcao.wmt.constant.AppConstants;
import com.squareup.okhttp.OkHttpClient;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class OAuthLoginTask extends AsyncTask<String, Void, String[]> {
	public interface OAuthLoginTaskListener {
		void onLoginUrlAvailable(String url);
		void onOAuthTaskFinished(String userName, String token);
		void onTaskError(Intent errorIntent);
	}

	@Inject
	protected GeocachingApi api;
	@Inject
	protected OAuthProvider oAuthProvider;
	@Inject
	protected OAuthConsumer oAuthConsumer;
	@Inject
	protected OkHttpClient okHttpClient;

	protected App app;
	protected Throwable exception;

	@Inject
	public OAuthLoginTask(App app) {
		this.app = app;
		app.inject(this);
	}

	private WeakReference<OAuthLoginTaskListener> oAuthLoginTaskListenerRef;

	public void setOAuthLoginTaskListener(OAuthLoginTaskListener oAuthLoginTaskListener) {
		this.oAuthLoginTaskListenerRef = new WeakReference<>(oAuthLoginTaskListener);
	}

	@Override
	protected String[] doInBackground(String... params) {
		exception = null;

		try {
			// we use server time for OAuth timestamp because device can have wrong timezone or time
			String timestamp = Long.toString(getServerDate(AppConstants.GEOCACHING_WEBSITE_URL).getTime() / 1000);

			if (params.length == 0) {
				String authUrl = oAuthProvider.retrieveRequestToken(oAuthConsumer, AppConstants.OAUTH_CALLBACK_URL, OAuth.OAUTH_TIMESTAMP, timestamp);
				return new String[] { authUrl };
			} else {
				oAuthProvider.retrieveAccessToken(oAuthConsumer, params[0], OAuth.OAUTH_TIMESTAMP, timestamp);

				// get account name
				api.openSession(oAuthConsumer.getToken());
				UserProfile userProfile = api.getYourUserProfile(false, false, false, false, false, false, DeviceInfoFactory.create(app));

				return new String[] {
								userProfile.getUser().getUserName(),
								api.getSession()
				};
			}
		} catch (OAuthExpectationFailedException e) {
			if (oAuthProvider.getResponseParameters().containsKey(AppConstants.OAUTH_ERROR_MESSAGE_PARAMETER)) {
				exception = new OAuthExpectationFailedException("Request token or token secret not set in server reply. "
								+ oAuthProvider.getResponseParameters().getFirst(AppConstants.OAUTH_ERROR_MESSAGE_PARAMETER));
				return null;
			}

			exception = e;
			return null;
		} catch (GeocachingApiException | OAuthMessageSignerException | OAuthNotAuthorizedException |OAuthCommunicationException e) {
			exception = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(String[] result) {
		if (exception != null) {
			onException(exception);
			return;
		}

		OAuthLoginTaskListener listener = oAuthLoginTaskListenerRef.get();

		if (result.length == 1) {

			if (listener != null) {
				listener.onLoginUrlAvailable(result[0]);
			}
		} else if (result.length == 2) {
			if (listener != null) {
				listener.onOAuthTaskFinished(result[0], result[1]);
			}
		}
	}

	protected void onException(Throwable t) {
		if (isCancelled())
			return;

		Timber.e(t, t.getMessage());

		// TODO Implement ExceptionHandler
		Intent intent = null; // new ExceptionHandler(app).handle(t);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);

		OAuthLoginTaskListener listener = oAuthLoginTaskListenerRef.get();
		if (listener != null) {
			listener.onTaskError(intent);
		}
	}

	private Date getServerDate(String url) throws NetworkException {
		try {
			Timber.i("Getting server time from url: " + url);
			HttpURLConnection c = okHttpClient.open(new URL(url));
			c.setRequestMethod("HEAD");
			c.setDoInput(false);
			c.setDoOutput(false);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String date = c.getHeaderField("Date");
				if (date != null) {
					Timber.i("We got time: " + date);
					return DateUtils.parseDate(date);
				}
			}
		} catch (IOException | DateParseException e) {
			throw new NetworkException(e.getMessage(), e);
		}

		Timber.e("No Date header found in a response, used device time instead.");
		return new Date();
	}
}
