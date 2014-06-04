package com.arcao.wmt.ui.fragment;

import com.arcao.wmt.ui.fragment.dialog.AddFavoritedTrackableDialogFragment;
import com.arcao.wmt.ui.fragment.dialog.OAuthLoginDialogFragment;

import dagger.Module;

@Module(
				injects = {
								TrackablesFragment.class,
								TrackableDetailFragment.class,
								TrackableMapFragment.class,
								TrackableStatisticsFragment.class,
								OAuthLoginDialogFragment.class,
								AddFavoritedTrackableDialogFragment.class
				},
				complete = false,
				library = true
)
public final class FragmentModule {

}