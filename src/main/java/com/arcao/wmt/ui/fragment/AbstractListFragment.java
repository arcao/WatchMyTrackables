package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import com.arcao.wmt.App;

public class AbstractListFragment extends ListFragment {
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		App.get(activity).inject(this);
	}
}