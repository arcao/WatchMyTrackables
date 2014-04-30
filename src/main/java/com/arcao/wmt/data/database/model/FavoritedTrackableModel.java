package com.arcao.wmt.data.database.model;

import android.provider.BaseColumns;
import com.activeandroid.annotation.Table;
import com.arcao.geocaching.api.data.Trackable;

@Table(name = "FavoritedTrackables", id = BaseColumns._ID)
public class FavoritedTrackableModel extends AbstractTrackableModel {
	public FavoritedTrackableModel() {
	}

	public FavoritedTrackableModel(Trackable trackable) {
		super(trackable);
	}
}
