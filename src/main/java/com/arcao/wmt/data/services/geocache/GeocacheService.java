package com.arcao.wmt.data.services.geocache;

import android.content.Context;
import android.os.Looper;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.utils.cache.Cache;
import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeocacheService {
	private final Context context;
	private final Executor executor;
	private final Executor downloadExecutor;
	protected final GeocachingApi geocachingApi;
	protected final Cache<String, SimpleGeocache> cache;
	protected final DispatcherHandler handler;
	protected final List<GetGeocacheRequest> requests;

	private GeocacheService(Context context, Executor executor, Executor downloadExecutor, GeocachingApi geocachingApi, Cache<String, SimpleGeocache> cache, DispatcherHandler handler) {
		this.context = context;
		this.executor = executor;
		this.downloadExecutor = downloadExecutor;
		this.geocachingApi = geocachingApi;
		this.cache = cache;
		this.handler = handler;

		requests = new ArrayList<>();
	}

	public GetGeocacheRequest getGeocache(final String cacheCode) {
		return getGeocache(cacheCode, null);
	}

	public GetGeocacheRequest getGeocache(final String cacheCode, final GetGeocacheTarget target) {
		final GetGeocacheRequest request = new GetGeocacheRequest(cacheCode, target);

		executor.execute(new Runnable() {
			@Override
			public void run() {
				SimpleGeocache data = cache.get(cacheCode);

				if (data != null) {
					handler.dispatchGetGeocache(request, data, null);
					return;
				}

				synchronized (requests) {
					requests.add(request);

					if (requests.size() == 1) {
						downloadExecutor.execute(new GeocacheDownloader(GeocacheService.this));
					}
				}

			}
		});

		return request;
	}

	public static class GeocacheServiceBuilder implements Builder<GeocacheService> {
		private final Context context;
		private final GeocachingApi geocachingApi;
		private Cache<String, SimpleGeocache> cache;

		public GeocacheServiceBuilder(Context context, GeocachingApi geocachingApi) {
			this.context = context.getApplicationContext();
			this.geocachingApi = geocachingApi;
		}

		public GeocacheServiceBuilder setCache(Cache<String, SimpleGeocache> cache) {
			this.cache = cache;
			return this;
		}

		@Override
		public GeocacheService build() {
			Executor downloadExecutor = Executors.newSingleThreadExecutor();
			Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			DispatcherHandler handler = new DispatcherHandler(Looper.getMainLooper());

			if (cache == null) {
				cache = new GeocacheFileCache(context);
			}

			return new GeocacheService(context, executor, downloadExecutor, geocachingApi, cache, handler);
		}
	}
}