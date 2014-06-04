package com.arcao.wmt.ui.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.activeandroid.content.ContentProvider;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.ui.adapter.TrackableCardCursorAdapter;
import com.arcao.wmt.ui.fragment.dialog.AddFavoritedTrackableDialogFragment;
import com.arcao.wmt.ui.task.UpdateFavoritedTrackablesTask;
import com.arcao.wmt.ui.task.UpdateMyTrackablesTask;
import com.arcao.wmt.ui.task.iface.FinishableTask;
import it.gmariotti.cardslib.library.internal.CardGridCursorAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.ref.WeakReference;

/**
 * Created by msloup on 11.5.2014.
 */
public class TrackablesFragment<M extends AbstractTrackableModel> extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor>, TrackableCardCursorAdapter.TrackableCardListener, SwipeRefreshLayout.OnRefreshListener, FinishableTask.OnFinishedListener {
	private static final String MODEL = "MODEL";
	private static final int TRACKABLE_LOADER = 100;

	public interface TrackablesListener {
		void onTrackableSelected(Class<? extends AbstractTrackableModel> modelClass, long id);
	}

	@Inject
	Provider<UpdateMyTrackablesTask> updateMyTrackablesTaskProvider;
	@Inject
	Provider<UpdateFavoritedTrackablesTask> updateFavoritedTrackablesTaskProvider;

	@InjectView(R.id.grid)
	CardGridView grid;

	@InjectView(R.id.swipe_container)
	SwipeRefreshLayout swipeLayout;

	private CardGridCursorAdapter mAdapter;
	private Class<M> modelClass;
	private WeakReference<TrackablesListener> trackablesListenerReference;
	private FinishableTask updateTask;

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

		mAdapter = new TrackableCardCursorAdapter<>(getActivity(), modelClass, this);

		grid.setAdapter(mAdapter);
		getActivity().getLoaderManager().initLoader(TRACKABLE_LOADER, null, this);

		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
						android.R.color.holo_green_light,
						android.R.color.holo_orange_light,
						android.R.color.holo_red_light);

		swipeLayout.setRefreshing(true);
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

		if (updateTask != null) {
			updateTask.cancel(true);
			updateTask = null;
		}

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
		swipeLayout.setRefreshing(false);
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
				onRefresh();
				return true;
			case R.id.action_add:
				AddFavoritedTrackableDialogFragment.newInstance().show(getFragmentManager(), AddFavoritedTrackableDialogFragment.class.getSimpleName());
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onRefresh() {
		swipeLayout.setRefreshing(true);

		if (updateTask != null) {
			updateTask.cancel(true);
		}

		if (modelClass == MyTrackableModel.class) {
			updateTask = (FinishableTask) updateMyTrackablesTaskProvider.get().execute();
		}
		else if (modelClass == FavoritedTrackableModel.class) {
			updateTask = (FinishableTask) updateFavoritedTrackablesTaskProvider.get().execute();
		}

		if (updateTask != null) {
			updateTask.setOnFinishedListener(this);
		}
	}

	@Override
	public void onFinished(Intent errorIntent) {
		swipeLayout.setRefreshing(false);
		updateTask = null;
	}

	@Override
	public void onTrackableCardClick(Class<? extends AbstractTrackableModel> modelClass, long id) {
		TrackablesListener listener = trackablesListenerReference.get();
		if (listener != null) {
			listener.onTrackableSelected(modelClass, id);
		}
	}
}
