package com.arcao.wmt.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.arcao.wmt.App;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.data.services.account.AccountService;
import com.arcao.wmt.ui.fragment.TrackableListFragment;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity implements TrackableListFragment.TrackableListListener {
	@Inject
	AccountService accountService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		App.get(this).inject(this);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		boolean showTabTitle = landscape || getResources().getBoolean(R.bool.actionbar_tab_title_show_always);

		ActionBar.Tab tab = actionBar.newTab()
						.setText(showTabTitle ? R.string.tab_my : R.string.tab_empty)
						.setContentDescription(R.string.tab_my)
						.setIcon(R.drawable.ic_action_social_person)
						.setTabListener(new TabListener(MyTrackableModel.class));
		actionBar.addTab(tab);
		actionBar.selectTab(tab);

		tab = actionBar.newTab()
						.setText(showTabTitle ? R.string.tab_favorited : R.string.tab_empty)
						.setIcon(R.drawable.ic_action_rating_favorite)
						.setContentDescription(R.string.tab_favorited)
						.setTabListener(new TabListener(FavoritedTrackableModel.class));
		actionBar.addTab(tab);

		if (!accountService.hasAccount()) {
			startActivityForResult(new Intent(this, WelcomeActivity.class), 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_base, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			finish();
		}
	}

	private Resources mResourcesImpl;

	@Override
	public Resources getResources() {
		if (mResourcesImpl == null) {
			mResourcesImpl = new FilteredResources(super.getResources());
		}
		return mResourcesImpl;
	}

	@Override
	public void onTrackableSelected(Class<? extends AbstractTrackableModel> modelClass, long id) {
		Timber.d(modelClass.getSimpleName() + ": " + id);
	}

	private static class FilteredResources extends Resources {
		private Set<Integer> mActionBarEmbedTabsIds = new HashSet<>();

		FilteredResources(Resources resources) {
			super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());

			mActionBarEmbedTabsIds.add(resources.getIdentifier("action_bar_embed_tabs", "bool", "android"));
			mActionBarEmbedTabsIds.add(resources.getIdentifier("action_bar_embed_tabs_pre_jb", "bool", "android"));
			mActionBarEmbedTabsIds.remove(0);
		}

		@Override
		public boolean getBoolean(int id) throws NotFoundException {
			return mActionBarEmbedTabsIds.contains(id) || super.getBoolean(id);
		}
	}

	private static class TabListener implements ActionBar.TabListener {
		protected final Class<? extends AbstractTrackableModel> modelClass;
		protected ListFragment listFragment;

		public TabListener(Class<? extends AbstractTrackableModel> modelClass) {
			this.modelClass = modelClass;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (listFragment == null) {
				listFragment = TrackableListFragment.newInstance(modelClass);
				fragmentTransaction.replace(R.id.content, listFragment, modelClass.getSimpleName());
			} else {
				fragmentTransaction.attach(listFragment);
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			fragmentTransaction.detach(listFragment);
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

		}
	}
}
