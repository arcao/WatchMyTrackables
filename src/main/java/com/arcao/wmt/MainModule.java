package com.arcao.wmt;

import android.app.Application;
import com.arcao.wmt.data.DataModule;
import com.arcao.wmt.ui.UiModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
				includes = {
								DataModule.class,
								UiModule.class
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