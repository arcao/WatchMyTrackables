package com.arcao.wmt.ui.task;

import dagger.Module;

@Module(
				injects = {
								OAuthLoginTask.class,
								UpdateMyTrackablesTask.class
				},
				complete = false,
				library = true
)
public final class TaskModule {
}
