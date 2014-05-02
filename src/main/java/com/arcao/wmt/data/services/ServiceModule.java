package com.arcao.wmt.data.services;

import android.app.Application;
import android.content.SharedPreferences;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.configuration.GeocachingApiConfiguration;
import com.arcao.geocaching.api.configuration.OAuthGeocachingApiConfiguration;
import com.arcao.geocaching.api.configuration.impl.ProductionGeocachingApiConfigurationImpl;
import com.arcao.geocaching.api.configuration.impl.StagingGeocachingApiConfigurationImpl;
import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.geocaching.api.downloader.OkHttpClientJsonDownloader;
import com.arcao.geocaching.api.impl.LiveGeocachingApi;
import com.arcao.geocaching.api.impl.live_geocaching_api.downloader.JsonDownloader;
import com.arcao.utils.cache.Cache;
import com.arcao.wmt.App;
import com.arcao.wmt.BuildConfig;
import com.arcao.wmt.data.services.account.AccountService;
import com.arcao.wmt.data.services.account.SharedPreferencesAccountService;
import com.arcao.wmt.data.services.geocache.GeocacheFileCache;
import com.arcao.wmt.data.services.geocache.GeocacheService;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
				complete = false,
				library = true
)
public final class ServiceModule {
	@Provides
	@Singleton
	OAuthGeocachingApiConfiguration provideOAuthGeocachingApiConfiguration() {
		if (BuildConfig.GEOCACHING_API_STAGING) {
			return new StagingGeocachingApiConfigurationImpl();
		} else {
			return new ProductionGeocachingApiConfigurationImpl();
		}
	}

	@Provides
	@Singleton
	GeocachingApiConfiguration provideGeocachingApiConfiguration(OAuthGeocachingApiConfiguration configuration) {
		return configuration;
	}

	@Provides
	@Singleton
	JsonDownloader provideJsonDownloader(GeocachingApiConfiguration configuration, OkHttpClient client) {
		return new OkHttpClientJsonDownloader(configuration, client);
	}

	@Provides
	@Singleton
	GeocachingApi provideGeocachingApi(GeocachingApiConfiguration configuration, JsonDownloader downloader) {
		return new LiveGeocachingApi.Builder().setConfiguration(configuration).setDownloader(downloader).build();
	}

	@Provides
	@Singleton
	Cache<String, SimpleGeocache> provideSimpleGeocacheCache(Application app) {
		return new GeocacheFileCache(app);
	}

	@Provides
	@Singleton
	GeocacheService provideGeocacheService(App app, GeocachingApi geocachingApi, Cache<String, SimpleGeocache> cache) {
		return new GeocacheService.GeocacheServiceBuilder(app, geocachingApi).setCache(cache).build();
	}

	@Provides
	@Singleton
	AccountService provideAccountService(@Named("Account") SharedPreferences prefs) {
		return new SharedPreferencesAccountService(prefs);
	}

	@Provides
	@Singleton
	CookieManager provideCookieManager(App app) {
		// This is to work around a bug where CookieManager may fail to instantiate if CookieSyncManager
		// has never been created.
		CookieSyncManager syncManager = CookieSyncManager.createInstance(app);
		syncManager.sync();

		return CookieManager.getInstance();
	}
}
