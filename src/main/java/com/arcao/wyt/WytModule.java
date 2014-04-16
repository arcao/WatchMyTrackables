package com.arcao.wyt;

import dagger.Module;

@Module(
				includes = {
				},
				injects = {
								WytApp.class
				}
)
public final class WytModule {
	private final WytApp app;

	public WytModule(WytApp app) {
		this.app = app;
	}

/*	@Provides
	@Singleton
	Application provideApplication() {
		return app;
	}*/
}