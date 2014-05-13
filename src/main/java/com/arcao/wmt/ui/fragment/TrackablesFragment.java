package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.activeandroid.content.ContentProvider;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.ui.adapter.TrackableCardCursorAdapter;
import com.arcao.wmt.ui.task.UpdateMyTrackablesTask;
import it.gmariotti.cardslib.library.internal.CardGridCursorAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.ref.WeakReference;

/**
 * Created by msloup on 11.5.2014.
 */
public class TrackablesFragment<M extends AbstractTrackableModel> extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
	private static final String MODEL = "MODEL";
	private static final int TRACKABLE_LOADER = 100;

	public interface TrackablesListener {
		void onTrackableSelected(Class<? extends AbstractTrackableModel> modelClass, long id);
	}

	@Inject
	Provider<UpdateMyTrackablesTask> updateMyTrackablesTaskProvider;

	@InjectView(R.id.grid)
	CardGridView grid;

	private CardGridCursorAdapter mAdapter;
	private Class<M> modelClass;
	private WeakReference<TrackablesListener> trackablesListenerReference;

	public TrackablesFragment() {
	}

	public static <M extends AbstractTrackableModel> TrackablesFragment<M> newInstance(Class<M> modelClass) {
		TrackablesFragment<M> fragment = new TrackablesFragment<>();

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
			trackablesListenerReference = new WeakReference<>((TrackablesListener)activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TrackablesListener");
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

		mAdapter = new TrackableCardCursorAdapter<>(getActivity(), modelClass);

		grid.setAdapter(mAdapter);
		grid.setOnItemClickListener(this);
		getActivity().getLoaderManager().initLoader(TRACKABLE_LOADER, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_trackables, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onDestroyView() {
		getActivity().getLoaderManager().destroyLoader(TRACKABLE_LOADER);
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
		//setListShown(true);
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
					//setListShown(false);
					updateMyTrackablesTaskProvider.get().execute();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TrackablesListener listener = trackablesListenerReference.get();
		if (listener != null) {
			listener.onTrackableSelected(modelClass, id);
		}
	}
}
