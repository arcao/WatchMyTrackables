package com.arcao.wmt.ui.task;

import android.os.AsyncTask;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.Trackable;
import com.arcao.geocaching.api.exception.GeocachingApiException;
import com.arcao.wmt.App;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.data.database.util.ModelUtils;
import com.arcao.wmt.ui.task.iface.FinishableTask;
import timber.log.Timber;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.List;

public class UpdateMyTrackablesTask extends AsyncTask<Void, Void, Void> implements FinishableTask {
	private static final int TRACKABLES_PER_REQUEST = 30;

	private GeocachingApi api;
	private App app;
	private WeakReference<OnFinishedListener> listenerRef;

	@Inject
	public UpdateMyTrackablesTask(App app, GeocachingApi api) {
		this.app = app;
		this.api = api;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			try {
				ActiveAndroid.beginTransaction();
				List<Trackable> trackables = api.getUsersTrackables(0, TRACKABLES_PER_REQUEST, 0, false);
				ModelUtils.truncate(MyTrackableModel.class);

				storeTrackables(trackables);

				// retrieve rest
				int start = 0;
				while (trackables.size() == TRACKABLES_PER_REQUEST) {
					start += TRACKABLES_PER_REQUEST;
					trackables = api.getUsersTrackables(start, TRACKABLES_PER_REQUEST, 0, false);
					storeTrackables(trackables);
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

	private void storeTrackables(List<Trackable> trackables) {
		for (Trackable trackable : trackables) {
			new MyTrackableModel(trackable).save();
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		if (listenerRef == null)
			return;

		OnFinishedListener listener = listenerRef.get();
		if (listener != null) {
			listener.onFinished(null);
		}
	}

	@Override
	public FinishableTask setOnFinishedListener(OnFinishedListener listener) {
		listenerRef = new WeakReference<>(listener);
		return this;
	}
}