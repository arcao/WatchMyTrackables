package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.ui.task.UpdateMyTrackablesTask;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;

public class TrackableListFragment<M extends AbstractTrackableModel> extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String MODEL = "MODEL";
	private static final int TRACKABLE_LOADER = 100;

	public interface TrackableListListener {
		void onTrackableSelected(Class<? extends AbstractTrackableModel> modelClass, long id);
	}

	@Inject
	Provider<UpdateMyTrackablesTask> updateMyTrackablesTaskProvider;

	private CursorAdapter mAdapter;
	private Class<M> modelClass;
	private WeakReference<TrackableListListener> trackableListListenerReference;

	public TrackableListFragment() {
	}

	public static <M extends AbstractTrackableModel> TrackableListFragment<M> newInstance(Class<M> modelClass) {
		TrackableListFragment<M> fragment = new TrackableListFragment<>();

		Bundle args = new Bundle();
		args.putString(MODEL, modelClass.getName());

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			trackableListListenerReference = new WeakReference<>((TrackableListListener)activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TrackableListListener");
		}

		try {
			modelClass = (Class<M>) Class.forName(getArguments().getString(MODEL));
		} catch (ClassNotFoundException e) {
			Timber.e(e, e.getMessage());
		}

		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new SimpleCursorAdapter(getActivity(),
						android.R.layout.simple_expandable_list_item_2,
						null,
						new String[]{"TrackingNumber", "Name"},
						new int[]{android.R.id.text1, android.R.id.text2},
						0);

		getListView().setAdapter(mAdapter);
		//getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		setListShown(false);
		getActivity().getSupportLoaderManager().initLoader(TRACKABLE_LOADER, null, this);
	}

	@Override
	public void onDestroyView() {
		getActivity().getSupportLoaderManager().destroyLoader(TRACKABLE_LOADER);
		super.onDestroyView();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
		switch (loaderID) {
			case TRACKABLE_LOADER:
				return new CursorLoader(getActivity().getApplicationContext(),
								ContentProvider.createUri(modelClass, null),
								null, null, null, null
				);
			default:
				// An invalid id was passed in
				return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		setListShown(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (modelClass == MyTrackableModel.class) {
			inflater.inflate(R.menu.fragment_trackables_my, menu);
		} else if (modelClass == FavoritedTrackableModel.class) {
			inflater.inflate(R.menu.fragment_trackables_favorited, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_refresh:
				if (modelClass == MyTrackableModel.class) {
					setListShown(false);
					updateMyTrackablesTaskProvider.get().execute();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TrackableListListener listener = trackableListListenerReference.get();
		if (listener != null) {
			listener.onTrackableSelected(modelClass, id);
		}
	}
}
