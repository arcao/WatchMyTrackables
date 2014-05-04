package com.arcao.wmt.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;

public class TrackableListFragment extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String MODEL = "MODEL";
	private static final int TRACKABLE_LOADER = 100;

	private CursorAdapter mAdapter;

	public TrackableListFragment() {
	}

	public static <T extends Model> ListFragment newInstance(Class<T> modelClass) {
		ListFragment fragment = new TrackableListFragment();

		Bundle args = new Bundle();
		args.putString(MODEL, modelClass.getName());

		fragment.setArguments(args);
		return fragment;
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
				try {
					Class<? extends Model> modelClass = (Class<? extends Model>) Class.forName(getArguments().getString(MODEL));
					return new CursorLoader(getActivity().getApplicationContext(),
									ContentProvider.createUri(modelClass, null),
									null, null, null, null
					);
				} catch (ClassNotFoundException e) {
					return null;
				}
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
}
