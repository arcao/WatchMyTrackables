package com.arcao.wmt.ui.fragment;

import com.arcao.wmt.data.database.model.AbstractTrackableModel;

/**
 * Created by Arcao on 31. 5. 2014.
 */
public class TrackableMapFragment extends AbstractTrackableFragment {
	public static <M extends AbstractTrackableModel> TrackableMapFragment newInstance(Class<M> modelClass, int id) {
		TrackableMapFragment fragment = new TrackableMapFragment();
		fragment.setArguments(createArguments(modelClass, id));
		return fragment;
	}
}
