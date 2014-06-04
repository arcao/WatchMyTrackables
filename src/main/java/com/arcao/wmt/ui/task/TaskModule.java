package com.arcao.wmt.ui.task;

import dagger.Module;

@Module(
				injects = {
								OAuthLoginTask.class,
								UpdateMyTrackablesTask.class,
								UpdateFavoritedTrackablesTask.class,
								AddFavoritedTrackableTask.class
				},
				complete = false,
				library = true
)
public final class TaskModule {
}
