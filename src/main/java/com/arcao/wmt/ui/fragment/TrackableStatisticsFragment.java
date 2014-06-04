package com.arcao.wmt.ui.fragment;

import com.arcao.wmt.data.database.model.AbstractTrackableModel;

/**
 * Created by Arcao on 31. 5. 2014.
 */
public class TrackableStatisticsFragment extends AbstractTrackableFragment {
	public static <M extends AbstractTrackableModel> TrackableStatisticsFragment newInstance(Class<M> modelClass, long id) {
		TrackableStatisticsFragment fragment = new TrackableStatisticsFragment();
		fragment.setArguments(createArguments(modelClass, id));
		return fragment;
	}

}
