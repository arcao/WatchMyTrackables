package com.arcao.wmt.data.database.model;

import com.activeandroid.annotation.Table;
import com.arcao.geocaching.api.data.Trackable;

/**
 * Created by msloup on 27.4.2014.
 */
@Table(name = "FavoritedTrackables")
public class FavoritedTrackableModel extends AbstractTrackableModel {
	public FavoritedTrackableModel() {
	}

	public FavoritedTrackableModel(Trackable trackable) {
		super(trackable);
	}
}
