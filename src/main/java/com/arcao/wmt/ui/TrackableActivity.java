package com.arcao.wmt.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.ui.fragment.TrackableDetailFragment;
import com.arcao.wmt.ui.fragment.TrackableMapFragment;
import com.arcao.wmt.ui.fragment.TrackableStatisticsFragment;

import timber.log.Timber;

public class TrackableActivity extends Activity {
	private static final String PARAM_MODEL = "MODEL";
	private static final String PARAM_ID = "ID";

	protected Class<? extends AbstractTrackableModel> modelClass;
	protected int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			modelClass = (Class<AbstractTrackableModel>) Class.forName(getIntent().getStringExtra(PARAM_MODEL));
		} catch (ClassNotFoundException e) {
			Timber.e(e, e.getMessage());
		}

		id = getIntent().getIntExtra(PARAM_ID, 0);

		setContentView(R.layout.activity_trackable);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		addTab(R.string.tab_trackable, TrackableDetailFragment.newInstance(modelClass, id));
		addTab(R.string.tab_map, TrackableMapFragment.newInstance(modelClass, id));
		addTab(R.string.tab_statistics, TrackableStatisticsFragment.newInstance(modelClass, id));
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
