package com.arcao.wmt;

import android.app.Application;
import android.content.Context;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import timber.log.Timber;

import javax.inject.Inject;

public class App extends Application {
	private ObjectGraph objectGraph;
	@Inject	Configuration databaseConfiguration;

	@Override public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}

		buildObjectGraphAndInject();
		ActiveAndroid.initialize(databaseConfiguration);
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
}