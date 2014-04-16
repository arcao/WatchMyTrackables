package com.arcao.wmt;

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