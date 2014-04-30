package com.arcao.wmt.ui;

import com.arcao.wmt.ui.fragment.TrackableListFragment;
import dagger.Module;

@Module(
				injects = {
								MainActivity.class,
								TrackableListFragment.class
				},
				complete = false,
				library = true
)
public class UiModule {

}
