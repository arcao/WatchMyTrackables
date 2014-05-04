package com.arcao.wmt.data.services.geocache;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.geocaching.api.impl.live_geocaching_api.filter.CacheCodeFilter;
import com.arcao.geocaching.api.impl.live_geocaching_api.filter.Filter;
import com.arcao.utils.concurrent.FutureTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GeocacheDownloader implements Runnable {
	private static final long WAIT_BEFORE_REQUEST_MS = 100;
	private static final int GEOCACHES_PER_REQUEST = 50;

	private final GeocacheService service;

	public GeocacheDownloader(GeocacheService service) {
		this.service = service;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(WAIT_BEFORE_REQUEST_MS);

			List<FutureTask<String, SimpleGeocache>> tasks;
			synchronized (service.tasks) {
				tasks = new ArrayList<>(CollectionUtils.select(service.tasks, REQUEST_FILTER));
				service.tasks.clear();
			}

			int current = 0;
			int perPage;
			final int count = tasks.size();

			while (current < count) {
				perPage = (count - current < GEOCACHES_PER_REQUEST) ? count - current : GEOCACHES_PER_REQUEST;

				List<SimpleGeocache> geocaches = service.geocachingApi.searchForGeocaches(false, perPage, 0, 0, new Filter[]{
								new CacheCodeFilter(getCacheCodePerPage(tasks, current, GEOCACHES_PER_REQUEST))
				});

				service.cache.putAll(geocaches);

				for (SimpleGeocache geocache : geocaches) {
					FutureTask<String, SimpleGeocache> task = getTaskByKey(tasks, geocache.getCacheCode());
					service.handler.dispatchResult(task, geocache, null);
				}
			}
		} catch (Exception e) {
			Timber.d(e, e.getMessage());
		}
	}

	private FutureTask<String, SimpleGeocache> getTaskByKey(List<FutureTask<String, SimpleGeocache>> tasks, String key) {
		for (FutureTask<String, SimpleGeocache> task : tasks) {
			if (key.equals(task.getKey()))
				return task;
		}

		return null;
	}

	private String[] getCacheCodePerPage(List<FutureTask<String, SimpleGeocache>> task, int current, int maxPerPage) {
		int count = (task.size() - current < maxPerPage) ? task.size() - current : maxPerPage;

		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = task.get(current + count).getKey();
		}

		return result;
	}

	private Predicate<FutureTask<String, SimpleGeocache>> REQUEST_FILTER = new Predicate<FutureTask<String, SimpleGeocache>>() {
		@Override
		public boolean evaluate(FutureTask<String, SimpleGeocache> object) {
			return !object.isCancelled();
		}
	};
}
