package com.arcao.wmt.ui;

import com.arcao.wmt.ui.fragment.TrackableListFragment;
import com.arcao.wmt.ui.fragment.dialog.OAuthLoginDialogFragment;
import com.arcao.wmt.ui.task.OAuthLoginTask;

import dagger.Module;

@Module(
				injects = {
								MainActivity.class,
								WelcomeActivity.class,
								TrackableListFragment.class,
								OAuthLoginDialogFragment.class,
								OAuthLoginTask.class
				},
				complete = false,
				library = true
)
public class UiModule {

}
