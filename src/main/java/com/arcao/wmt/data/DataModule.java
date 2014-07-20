package com.arcao.wmt.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.arcao.wmt.data.database.DatabaseModule;
import com.arcao.wmt.data.services.ServiceModule;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(
				includes = {
								DatabaseModule.class,
								ServiceModule.class
				},
				complete = false,
				library = true
)
public final class DataModule {
	static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

	@Provides
	@Singleton
	SharedPreferences provideSharedPreferences(Application app) {
		return PreferenceManager.getDefaultSharedPreferences(app);
	}

	@Provides
	@Singleton
	@Named("Account")
	SharedPreferences provideAccountSharedPreferences(Application app) {
		return app.getSharedPreferences("Account", Context.MODE_PRIVATE);
	}

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(Application app) {
		return createOkHttpClient(app);
	}

	@Provides
	@Singleton
	Picasso providePicasso(Application app, OkHttpClient client) {
		return new Picasso.Builder(app)
						.downloader(new OkHttpDownloader(client))
						.listener(new Picasso.Listener() {
							@Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
								Timber.e(e, "Failed to load image: %s", uri);
							}
						})
						.build();
	}

	static OkHttpClient createOkHttpClient(Application app) {
		OkHttpClient client = new OkHttpClient();

		// Install an HTTP cache in the application cache directory.
		try {
			File cacheDir = new File(app.getCacheDir(), "http");
			Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
			client.setCache(cache);
		} catch (IOException e) {
			Timber.e(e, "Unable to install disk cache.");
		}

		return client;
	}
}
