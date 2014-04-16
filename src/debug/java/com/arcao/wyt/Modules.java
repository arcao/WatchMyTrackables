package com.arcao.wyt;

/**
 * Created by msloup on 16.4.2014.
 */
final class Modules {
	static Object[] list(WytApp app) {
		return new Object[] {
						new WytModule(app)
		};
	}

	private Modules() {
		// No instances.
	}
}