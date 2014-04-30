package com.arcao.wmt.data.database.model;

import android.provider.BaseColumns;
import com.activeandroid.annotation.Table;
import com.arcao.geocaching.api.data.Trackable;

@Table(name = "MyTrackables", id = BaseColumns._ID)
public class MyTrackableModel extends AbstractTrackableModel {
	public MyTrackableModel() {
	}

	public MyTrackableModel(Trackable trackable) {
		super(trackable);
	}
}
