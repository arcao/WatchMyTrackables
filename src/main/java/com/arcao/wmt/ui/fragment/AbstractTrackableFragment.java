package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.os.Bundle;

import com.arcao.wmt.data.database.model.AbstractTrackableModel;

import timber.log.Timber;

public class AbstractTrackableFragment extends AbstractFragment {
	private static final String ARG_MODEL = "MODEL";
	private static final String ARG_ID = "ID";

	protected Class<? extends AbstractTrackableModel> modelClass;
	protected int id;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			modelClass = (Class<AbstractTrackableModel>) Class.forName(getArguments().getString(ARG_MODEL));
		} catch (ClassNotFoundException e) {
			Timber.e(e, e.getMessage());
		}

		id = getArguments().getInt(ARG_ID);
	}

	protected static <M extends AbstractTrackableModel> Bundle createArguments(Class<M> modelClass, int id) {
		Bundle args = new Bundle();
		args.putString(ARG_MODEL, modelClass.getName());
		args.putInt(ARG_ID, id);
		return args;
	}
}
