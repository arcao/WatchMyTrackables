package com.arcao.wmt;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.UUID;

public class App extends Application {
	private ObjectGraph objectGraph;
	@Inject	Configuration databaseConfiguration;
	@Inject SharedPreferences prefs;
	private String deviceId;

	@Override public void onCreate() {
		super.onCreate();

		//if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		//}

		buildObjectGraphAndInject();
		ActiveAndroid.initialize(databaseConfiguration, BuildConfig.DEBUG);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		ActiveAndroid.dispose();
	}

	@DebugLog
	public void buildObjectGraphAndInject() {
		objectGraph = ObjectGraph.create(Modules.list(this));
		objectGraph.inject(this);
	}

	public void inject(Object o) {
		objectGraph.inject(o);
	}

	public static App get(Context context) {
		return (App) context.getApplicationContext();
	}

	public String getDeviceId() {
		if (deviceId == null) {
			deviceId = prefs.getString("device_id", null);

			if (deviceId == null) {
				deviceId = UUID.randomUUID().toString();
				prefs.edit().putString("device_id", deviceId).apply();
			}
		}

		return deviceId;
	}

}