package com.arcao.wmt.data.services.geocache;

import android.content.Context;
import android.os.Looper;
import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.utils.cache.Cache;
import com.arcao.utils.concurrent.FutureCallback;
import com.arcao.utils.concurrent.FutureTask;

import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeocacheService {
	private final Executor executor;
	private final Executor downloadExecutor;
	protected final GeocachingApi geocachingApi;
	protected final Cache<String, SimpleGeocache> cache;
	protected final DispatcherHandler handler;
	protected final List<FutureTask<String, SimpleGeocache>> tasks;

	private GeocacheService(Executor executor, Executor downloadExecutor, GeocachingApi geocachingApi, Cache<String, SimpleGeocache> cache, DispatcherHandler handler) {
		this.executor = executor;
		this.downloadExecutor = downloadExecutor;
		this.geocachingApi = geocachingApi;
		this.cache = cache;
		this.handler = handler;

		tasks = new ArrayList<>();
	}

	public FutureTask<String, SimpleGeocache> getGeocache(final String cacheCode) {
		return getGeocache(cacheCode, null);
	}

	public FutureTask<String, SimpleGeocache> getGeocache(final String cacheCode, final FutureCallback<SimpleGeocache> callback) {
		final FutureTask<String, SimpleGeocache> task = new FutureTask<>(cacheCode, callback);

		executor.execute(new Runnable() {
			@Override
			public void run() {
				SimpleGeocache data = cache.get(cacheCode);

				if (data != null) {
					handler.dispatchResult(task, data, null);
					return;
				}

				synchronized (tasks) {
					tasks.add(task);

					if (tasks.size() == 1) {
						downloadExecutor.execute(new GeocacheDownloader(GeocacheService.this));
					}
				}

			}
		});

		return task;
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

			return new GeocacheService(executor, downloadExecutor, geocachingApi, cache, handler);
		}
	}
}