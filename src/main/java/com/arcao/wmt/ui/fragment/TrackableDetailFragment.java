package com.arcao.wmt.ui.fragment;

import com.arcao.wmt.data.database.model.AbstractTrackableModel;

/**
 * Created by Arcao on 31. 5. 2014.
 */
public class TrackableDetailFragment extends AbstractTrackableFragment {
	public static <M extends AbstractTrackableModel> TrackableDetailFragment newInstance(Class<M> modelClass, int id) {
		TrackableDetailFragment fragment = new TrackableDetailFragment();
		fragment.setArguments(createArguments(modelClass, id));
		return fragment;
	}
}
