package com.arcao.wmt.ui;

import com.arcao.wmt.ui.fragment.FragmentModule;
import com.arcao.wmt.ui.task.TaskModule;

import dagger.Module;

@Module(
				includes = {
								FragmentModule.class,
								TaskModule.class
				},
				injects = {
								MainActivity.class,
								WelcomeActivity.class,
				},
				complete = false,
				library = true
)
public final class UiModule {

}
