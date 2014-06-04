package com.arcao.wmt.ui;

import com.arcao.wmt.ui.fragment.FragmentModule;
import com.arcao.wmt.ui.task.TaskModule;

import com.arcao.wmt.ui.widget.WidgetModule;
import dagger.Module;

@Module(
				includes = {
								FragmentModule.class,
								TaskModule.class,
								WidgetModule.class
				},
				injects = {
								MainActivity.class,
								WelcomeActivity.class,
								TrackableActivity.class
				},
				complete = false,
				library = true
)
public final class UiModule {

}
