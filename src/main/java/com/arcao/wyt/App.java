package com.arcao.wyt;

import android.app.Application;
import android.content.Context;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class App extends Application {
	private ObjectGraph objectGraph;

	@Override public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}

		buildObjectGraphAndInject();
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