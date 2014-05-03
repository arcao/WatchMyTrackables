package com.arcao.wmt.ui.task;

import android.os.AsyncTask;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Delete;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.Trackable;
import com.arcao.geocaching.api.exception.GeocachingApiException;
import com.arcao.wmt.App;
import com.arcao.wmt.data.database.model.MyTrackableModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class UpdateMyTrackablesTask extends AsyncTask<Void, Void, Void> {
	private GeocachingApi api;
	private App app;

	@Inject
	public UpdateMyTrackablesTask(App app, GeocachingApi api) {
		this.app = app;
		this.api = api;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			new Delete().from(MyTrackableModel.class).execute();
			app.getContentResolver().notifyChange(ContentProvider.createUri(MyTrackableModel.class, null), null);

			List<Trackable> trackables = api.getUsersTrackables(0, 30, 0, false);
			ActiveAndroid.beginTransaction();
			try {
				for (Trackable trackable : trackables) {
					new MyTrackableModel(trackable).save();
				}
				ActiveAndroid.setTransactionSuccessful();
			}
			finally {
				ActiveAndroid.endTransaction();
			}

			app.getContentResolver().notifyChange(ContentProvider.createUri(MyTrackableModel.class, null), null);
		} catch (GeocachingApiException e) {
			Timber.e(e, e.getMessage());
		}

		return null;
	}
}