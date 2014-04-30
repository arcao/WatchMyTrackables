package com.arcao.wmt.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Delete;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.Trackable;
import com.arcao.geocaching.api.exception.GeocachingApiException;
import com.arcao.wmt.App;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.ui.fragment.TrackableListFragment;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class MainActivity extends ActionBarActivity {
	@Inject
	Provider<UpdateTask> updateTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		App.get(this).inject(this);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();

		getSupportFragmentManager().beginTransaction().replace(R.id.content, TrackableListFragment.newInstance(MyTrackableModel.class)).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_refresh:
				updateTask.get().execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected static class UpdateTask extends AsyncTask<Void, Void, Void> {
		private GeocachingApi api;
		private App app;

		@Inject
		public UpdateTask(App app, GeocachingApi api) {
			this.app = app;
			this.api = api;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// TODO for testing purposes only
				api.openSession("hFHmU3c7wnljwzCg4QodthxIuMo=");

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
}
