package com.arcao.wmt.data.database.model;

import com.activeandroid.annotation.Table;
import com.arcao.geocaching.api.data.Trackable;

/**
 * Created by msloup on 27.4.2014.
 */
@Table(name = "MyTrackables")
public class MyTrackableModel extends AbstractTrackableModel {
	public MyTrackableModel() {
	}

	public MyTrackableModel(Trackable trackable) {
		super(trackable);
	}
}
