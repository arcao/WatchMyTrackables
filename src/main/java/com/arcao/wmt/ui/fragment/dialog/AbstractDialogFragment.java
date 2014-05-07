package com.arcao.wmt.ui.fragment.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import com.arcao.wmt.App;

public abstract class AbstractDialogFragment extends DialogFragment {
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		App.get(activity).inject(this);
	}

	// This is to work around what is apparently a bug. If you don't have it
	// here the dialog will be dismissed on rotation, so tell it not to dismiss.
	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);

		super.onDestroyView();
	}

	public boolean isShowing() {
		return getDialog() != null && getDialog().isShowing();
	}
}
