package com.arcao.wmt;

import android.app.Application;

import com.arcao.wmt.data.DataModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
				includes = {
								DataModule.class
				},
				injects = {
								App.class
				}
)
public final class MainModule {
	private final App app;

	public MainModule(App app) {
		this.app = app;
	}

	@Provides
	@Singleton
	Application provideApplication() {
		return app;
	}
}