package com.arcao.wmt.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.activeandroid.Model;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.ui.fragment.TrackableDetailFragment;
import com.arcao.wmt.ui.fragment.TrackableMapFragment;
import com.arcao.wmt.ui.fragment.TrackableStatisticsFragment;
import timber.log.Timber;

public class TrackableActivity extends Activity {
	private static final String STATE_TAB = "TAB";
	private static final String PARAM_MODEL = "MODEL";
	private static final String PARAM_ID = "ID";

	protected Class<? extends AbstractTrackableModel> modelClass;
	protected long id;

	public static Intent createIntent(Context source, Class<? extends AbstractTrackableModel> modelClass, long id) {
		return new Intent(source, TrackableActivity.class)
						.putExtra(PARAM_MODEL, modelClass.getName())
						.putExtra(PARAM_ID, id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			modelClass = (Class<AbstractTrackableModel>) Class.forName(getIntent().getStringExtra(PARAM_MODEL));
		} catch (ClassNotFoundException e) {
			Timber.e(e, e.getMessage());
		}

		id = getIntent().getLongExtra(PARAM_ID, 0);

		setContentView(R.layout.activity_trackable);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		addTab(R.string.tab_trackable, TrackableDetailFragment.newInstance(modelClass, id));
		addTab(R.string.tab_map, TrackableMapFragment.newInstance(modelClass, id));
		addTab(R.string.tab_statistics, TrackableStatisticsFragment.newInstance(modelClass, id));

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_TAB, 0));
		}

		AbstractTrackableModel model = Model.load(modelClass, id);
		setTitle(model.trackingNumber);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_TAB, getActionBar().getSelectedNavigationIndex());
	}

	protected void addTab( int resTitle, Fragment fragment) {
		getActionBar().addTab(getActionBar().newTab().setText(resTitle).setTabListener(new TabListener(fragment)));
	}

	private static class TabListener implements ActionBar.TabListener {
		protected Fragment fragment;
		protected boolean initialized = false;

		public TabListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (!initialized) {
				fragmentTransaction.replace(R.id.content, fragment, fragment.getClass().getSimpleName());
				initialized = true;
			} else {
				fragmentTransaction.attach(fragment);
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			fragmentTransaction.detach(fragment);
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

		}
	}
}
