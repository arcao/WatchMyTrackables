package com.arcao.wmt.data.services.geocache;

import com.arcao.geocaching.api.data.SimpleGeocache;

import java.lang.ref.WeakReference;
import java.util.concurrent.*;

public class GetGeocacheRequest implements Future<SimpleGeocache> {
	private final String cacheCode;
	private final WeakReference<GetGeocacheTarget> targetRef;
	private boolean cancelled = false;
	private boolean done = false;
	private SimpleGeocache value;
	private Throwable exception;

	public GetGeocacheRequest(String cacheCode, GetGeocacheTarget target) {
		this.cacheCode = cacheCode;
		this.targetRef = new WeakReference<>(target);
	}

	public String getCacheCode() {
		return cacheCode;
	}

	public void cancel() {
		cancel(false);
	}

	@Override
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {
		return cancelled = true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public synchronized SimpleGeocache get() throws InterruptedException, ExecutionException {
		while (!done && !cancelled) {
			((Object)this).wait();
		}
		return report();
	}

	@Override
	public synchronized SimpleGeocache get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long deadLine = unit.toMillis(timeout);

		while (!done && !cancelled && deadLine - System.currentTimeMillis() > 0L) {
			((Object)this).wait(deadLine - System.currentTimeMillis());
		}

		if (deadLine <= System.currentTimeMillis() && !done) {
			throw new TimeoutException();
		}

		return report();
	}

	private SimpleGeocache report() throws ExecutionException {
		if (cancelled)
			throw new CancellationException();
		if (exception != null)
			throw new ExecutionException(exception);

		return value;
	}

	protected void publish(SimpleGeocache result, Throwable exception) {
		synchronized (this) {
			this.value = result;
			this.exception = exception;
			this.done = true;
			((Object)this).notifyAll();
		}

		GetGeocacheTarget target = targetRef.get();
		if (target != null && !cancelled) {
			target.onGetGeocache(result);
		}
	}

}
