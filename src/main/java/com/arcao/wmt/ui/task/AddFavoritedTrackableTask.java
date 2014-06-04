package com.arcao.wmt.ui.task;

import android.os.AsyncTask;
import com.activeandroid.query.Select;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.Trackable;
import com.arcao.wmt.App;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.ui.task.iface.FinishableTask;
import timber.log.Timber;

import javax.inject.Inject;
import java.lang.ref.WeakReference;

public class AddFavoritedTrackableTask extends AsyncTask<String, Void, Throwable> implements FinishableTask {
	private GeocachingApi geocachingApi;
	private App app;
	private WeakReference<FinishableTask.OnFinishedListener> listenerRef;

	@Inject
	public AddFavoritedTrackableTask(App app, GeocachingApi geocachingApi)
	{
		this.app = app;
		this.geocachingApi = geocachingApi;
	}

	protected Throwable doInBackground(String... params) {
		if ((params.length != 1) || (params[0] == null) || (params[0].length() == 0)) {
			return null;
		}

		if (new Select().from(FavoritedTrackableModel.class).where("TrackingNumber = ?", params[0]).exists()) {
			return null;
		}

		try {
			Trackable trackable = this.geocachingApi.getTrackable(params[0], 0);
			new FavoritedTrackableModel().apply(trackable).save();
		} catch (Throwable e) {
			Timber.e(e, "");
			return e;
		}

		return null;
	}

	protected void onPostExecute(Throwable result) {
		if (this.listenerRef != null) {
			FinishableTask.OnFinishedListener listener = this.listenerRef.get();
			if (listener != null) {
				listener.onFinished(null);
			}
		}
	}

	public FinishableTask setOnFinishedListener(FinishableTask.OnFinishedListener listener) {
		this.listenerRef = new WeakReference(listener);
		return this;
	}
}
