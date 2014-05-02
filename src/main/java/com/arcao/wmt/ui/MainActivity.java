package com.arcao.wmt.ui;

import android.content.Intent;
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
import com.arcao.wmt.data.services.account.AccountService;
import com.arcao.wmt.ui.fragment.TrackableListFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;

public class MainActivity extends ActionBarActivity {
	@Inject
	Provider<UpdateTask> updateTask;
	@Inject
	AccountService accountService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		App.get(this).inject(this);
		setContentView(R.layout.activity_main);

		if (!accountService.hasAccount()) {
			startActivityForResult(new Intent(this, WelcomeActivity.class), 0);
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			finish();
		}
	}
}
