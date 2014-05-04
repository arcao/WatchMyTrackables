package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.ui.task.UpdateMyTrackablesTask;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Provider;

public class TrackableListFragment extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TYPE = "TYPE";
	private static final int TRACKABLE_LOADER = 100;

	public interface TrackableListListener {
		void onTrackableListItemSelected(Uri itemUri);
	}

	public enum Type {
		My,
		Favorited
	}

	@Inject
	Provider<UpdateMyTrackablesTask> updateMyTrackablesTaskProvider;

	private CursorAdapter mAdapter;
	private Type type;
	private Class<? extends Model> modelClass;
	private WeakReference<TrackableListListener> trackableListListenerReference;

	public TrackableListFragment() {
	}

	public static ListFragment newInstance(Type type) {
		ListFragment fragment = new TrackableListFragment();

		Bundle args = new Bundle();
		args.putInt(TYPE, type.ordinal());

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			trackableListListenerReference = new WeakReference<>((TrackableListListener)activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TrackableListListener");
		}

		type = Type.values()[getArguments().getInt(TYPE)];

		switch(type) {
			case Favorited:
				modelClass = FavoritedTrackableModel.class;
				break;
			case My:
			default:
				modelClass = MyTrackableModel.class;
				break;
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
		switch (type) {
			case My:
				inflater.inflate(R.menu.fragment_trackables_my, menu);
				break;
			case Favorited:
				inflater.inflate(R.menu.fragment_trackables_favorited, menu);
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_refresh:
				if (type == Type.My) {
					setListShown(false);
					updateMyTrackablesTaskProvider.get().execute();
				} else {

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
			Uri uri = ContentProvider.createUri(modelClass, id);
			listener.onTrackableListItemSelected(uri);
		}
	}
}
