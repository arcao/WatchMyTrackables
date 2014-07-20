package com.arcao.wmt.ui.fragment;

import com.arcao.wmt.ui.fragment.dialog.DialogFragmentModule;

import dagger.Module;

@Module(
				includes = {
								DialogFragmentModule.class
				},
				injects = {
								TrackablesFragment.class,

								// trackable fragments
								TrackableDetailFragment.class,
								TrackableMapFragment.class,
								TrackableStatisticsFragment.class
				},
				complete = false,
				library = true
)
public final class FragmentModule {

}