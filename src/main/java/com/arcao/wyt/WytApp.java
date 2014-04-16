package com.arcao.wyt;

import android.app.Application;
import android.content.Context;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class WytApp extends Application {
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

	public static WytApp get(Context context) {
		return (WytApp) context.getApplicationContext();
	}
}