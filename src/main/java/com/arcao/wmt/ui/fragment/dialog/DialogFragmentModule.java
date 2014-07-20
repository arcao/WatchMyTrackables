package com.arcao.wmt.ui.fragment.dialog;

import dagger.Module;

@Module(
				injects = {
								OAuthLoginDialogFragment.class,
								AddFavoritedTrackableDialogFragment.class,
				},
				complete = false,
				library = true
)
public final class DialogFragmentModule {
}
