package com.arcao.wmt.ui.task;

import android.os.AsyncTask;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.Trackable;
import com.arcao.geocaching.api.exception.GeocachingApiException;
import com.arcao.wmt.App;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.ui.task.iface.FinishableTask;
import timber.log.Timber;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.List;

public class UpdateFavoritedTrackablesTask extends AsyncTask<Void, Void, Void> implements FinishableTask {
	private GeocachingApi api;
	private App app;
	private WeakReference<OnFinishedListener> listenerRef;

	@Inject
	public UpdateFavoritedTrackablesTask(App app, GeocachingApi api) {
		this.app = app;
		this.api = api;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			try {
				ActiveAndroid.beginTransaction();
				List<FavoritedTrackableModel> items = new Select().from(FavoritedTrackableModel.class).execute();
				for (FavoritedTrackableModel item : items) {
					Trackable trackable = api.getTrackable(item.trackingNumber, 0);
					if (trackable == null)
						continue;

					item.apply(trackable);
					item.save();
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