package com.arcao.wyt;

import dagger.Module;

@Module(
				includes = {
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

/*	@Provides
	@Singleton
	Application provideApplication() {
		return app;
	}*/
}