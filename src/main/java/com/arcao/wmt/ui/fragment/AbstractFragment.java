package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import com.arcao.wmt.App;

public abstract class AbstractFragment extends Fragment {
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		App.get(activity).inject(this);
	}
}
