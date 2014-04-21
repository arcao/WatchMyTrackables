package com.arcao.wmt.data.services.geocache;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.geocaching.api.impl.live_geocaching_api.filter.CacheCodeFilter;
import com.arcao.geocaching.api.impl.live_geocaching_api.filter.Filter;

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

			List<GetGeocacheRequest> processedRequests;
			synchronized (service.requests) {
				processedRequests = new ArrayList<>(service.requests);
				service.requests.clear();
			}

			int current = 0;
			int perPage;
			final int count = processedRequests.size();

			while (current < count) {
				perPage = (count - current < GEOCACHES_PER_REQUEST) ? count - current : GEOCACHES_PER_REQUEST;

				List<SimpleGeocache> geocaches = service.geocachingApi.searchForGeocaches(false, perPage, 0, 0, new Filter[]{
								new CacheCodeFilter(getCacheCodePerPage(processedRequests, current, GEOCACHES_PER_REQUEST))
				});

				for (SimpleGeocache geocache : geocaches) {
					GetGeocacheRequest request = getRequestByCacheCode(processedRequests, geocache.getCacheCode());
					service.handler.dispatchGetGeocache(request, geocache);
					service.cache.put(geocache);
				}
			}
		} catch (Exception e) {
			Timber.d(e, e.getMessage());
		}
	}

	private GetGeocacheRequest getRequestByCacheCode(List<GetGeocacheRequest> requests, String cacheCode) {
		for (GetGeocacheRequest request : requests) {
			if (cacheCode.equals(request.getCacheCode()))
				return request;
		}

		return null;
	}

	private String[] getCacheCodePerPage(List<GetGeocacheRequest> requests, int current, int maxPerPage) {
		int count = (requests.size() - current < maxPerPage) ? requests.size() - current : maxPerPage;

		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = requests.get(current + count).getCacheCode();
		}

		return result;
	}
}
