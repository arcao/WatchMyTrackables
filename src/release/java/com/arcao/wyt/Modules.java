package com.arcao.wyt;

/**
 * Created by msloup on 16.4.2014.
 */
final class Modules {
	static Object[] list(App app) {
		return new Object[] {
						new MainModule(app)
		};
	}

	private Modules() {
		// No instances.
	}
}