package com.arcao.wmt.ui.fragment;

import com.arcao.wmt.ui.fragment.dialog.OAuthLoginDialogFragment;

import dagger.Module;

@Module(
				injects = {
								TrackableListFragment.class,
								OAuthLoginDialogFragment.class,
				},
				complete = false,
				library = true
)
public final class FragmentModule {

}