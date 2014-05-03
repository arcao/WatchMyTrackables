package com.arcao.wmt.ui.fragment.dialog;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.arcao.wmt.App;
import com.arcao.wmt.R;
import com.arcao.wmt.constant.AppConstants;
import com.arcao.wmt.data.services.account.AccountService;
import com.arcao.wmt.ui.task.OAuthLoginTask;

import java.lang.ref.WeakReference;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import oauth.signpost.OAuth;
import timber.log.Timber;

public class OAuthLoginDialogFragment extends AbstractDialogFragment implements OAuthLoginTask.OAuthLoginTaskListener {
	private static final String STATE_PROGRESS_VISIBLE = "STATE_PROGRESS_VISIBLE";

	public interface OnTaskFinishedListener {
		void onTaskFinished(Intent errorIntent);
	}

	@Inject
	protected Provider<OAuthLoginTask> loginTaskProvider;
	@Inject
	protected App app;
	@Inject
	protected AccountService accountService;
	@Inject
	protected CookieManager cookieManager;

	@InjectView(R.id.webview_holder)
	protected FrameLayout webViewHolder;
	@InjectView(R.id.progress_holder)
	protected View progressHolder;


	protected WeakReference<OnTaskFinishedListener> taskFinishedListenerRef;
	protected WebView webView = null;
	protected Bundle lastInstanceState;
	protected OAuthLoginTask mTask;

	public static OAuthLoginDialogFragment newInstance() {
		return new OAuthLoginDialogFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		setRetainInstance(true);

		// clear geocaching.com cookies
		clearGeocachingCookies();

		mTask = loginTaskProvider.get();
		mTask.setOAuthLoginTaskListener(this);
		mTask.execute();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			taskFinishedListenerRef = new WeakReference<>((OnTaskFinishedListener) activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnTaskFinishListener");
		}
	}

	private void clearGeocachingCookies() {
		// setCookie acts differently when trying to expire cookies between builds of Android that are using
		// Chromium HTTP stack and those that are not. Using both of these domains to ensure it works on both.
		clearCookiesForDomain(cookieManager, "geocaching.com");
		clearCookiesForDomain(cookieManager, ".geocaching.com");
		clearCookiesForDomain(cookieManager, "https://geocaching.com");
		clearCookiesForDomain(cookieManager, "https://.geocaching.com");
	}

	private static void clearCookiesForDomain(CookieManager cookieManager, String domain) {
		String cookies = cookieManager.getCookie(domain);
		if (cookies == null) {
			return;
		}

		String[] splitCookies = cookies.split(";");
		for (String cookie : splitCookies) {
			String[] cookieParts = cookie.split("=");
			if (cookieParts.length > 0) {
				String newCookie = cookieParts[0].trim() + "=;expires=Sat, 1 Jan 2000 00:00:01 UTC;";
				cookieManager.setCookie(domain, newCookie);
			}
		}
		cookieManager.removeExpiredCookie();
	}


	@Override
	public void onLoginUrlAvailable(String url) {
		if (webView != null) {
			webView.loadUrl(url);
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);

		if (mTask != null && !mTask.isCancelled())
			mTask.cancel(true);

		OnTaskFinishedListener listener = taskFinishedListenerRef.get();
		if (listener != null) {
			listener.onTaskFinished(null);
		}
	}

	@Override
	public void onOAuthTaskFinished(String userName, String token) {
		accountService.removeAccount();

		accountService.addAccount(new Account(userName, AccountService.ACCOUNT_TYPE));
		accountService.setAuthToken(token);

		OnTaskFinishedListener listener = taskFinishedListenerRef.get();
		if (listener != null) {
			listener.onTaskFinished(null);
		}
	}

	@Override
	public void onTaskError(Intent errorIntent) {
		OnTaskFinishedListener listener = taskFinishedListenerRef.get();
		if (listener != null) {
			listener.onTaskFinished(errorIntent);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (webView != null) {
			webView.saveState(outState);
		}

		if (progressHolder != null) {
			outState.putInt(STATE_PROGRESS_VISIBLE, progressHolder.getVisibility());
			Timber.d("setVisibility: " + progressHolder.getVisibility());
		}

		lastInstanceState = outState;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// FIX savedInstanceState is null after rotation change
		if (savedInstanceState == null)
			savedInstanceState = lastInstanceState;

		View view = inflater.inflate(R.layout.fragment_oauth, container);
		ButterKnife.inject(this, view);

		progressHolder.setVisibility(View.VISIBLE);

		if (savedInstanceState != null) {
			progressHolder.setVisibility(savedInstanceState.getInt(STATE_PROGRESS_VISIBLE, View.VISIBLE));
		}

		webView = createWebView(savedInstanceState);
		webViewHolder.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		return view;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public WebView createWebView(Bundle savedInstanceState) {
		WebView webView = new FixedWebView(getActivity());

		//webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(new DialogWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSavePassword(false);

		if (savedInstanceState != null)
			webView.restoreState(savedInstanceState);

		return webView;
	}

	private class DialogWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(AppConstants.OAUTH_CALLBACK_URL)) {
				Uri uri = Uri.parse(url);

				if (progressHolder != null) {
					progressHolder.setVisibility(View.VISIBLE);
				}

				mTask = loginTaskProvider.get();
				mTask.setOAuthLoginTaskListener(OAuthLoginDialogFragment.this);
				mTask.execute(uri.getQueryParameter(OAuth.OAUTH_VERIFIER));

				return true;
			}

			if (!isOAuthUrl(url)) {
				Timber.d("External URL: " + url);

				// launch external URLs in a full browser
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				app.startActivity(intent);
				return true;
			}

			return false;
		}

		protected boolean isOAuthUrl(String url) {
			url = url.toLowerCase(Locale.US);

			return url.contains("/oauth/") ||
							url.contains("/mobileoauth/") ||
							url.contains("/mobilesignin/") ||
							url.contains("/mobilecontent/") ||
							url.contains("//m.facebook");
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);

			// TODO show error 
			/*if (getActivity() != null)
				onTaskError(ErrorActivity.createErrorIntent(getActivity(), 0, description, false, null));*/
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (progressHolder != null) {
				progressHolder.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			if (progressHolder != null && !url.startsWith(AppConstants.OAUTH_CALLBACK_URL)) {
				progressHolder.setVisibility(View.GONE);
			}
		}
	}

	public static class FixedWebView extends WebView {
		public FixedWebView(Context context) {
			super (context);
		}

		@Override
		public void onWindowFocusChanged(boolean hasWindowFocus) {
			try {
				super.onWindowFocusChanged(hasWindowFocus);
			} catch (NullPointerException e) {
				// Catch null pointer exception
				// suggested workaround from Web
			}
		}
	}
}
