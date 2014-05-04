package com.arcao.utils.concurrent;

import java.lang.ref.WeakReference;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTask<Key, Value> implements Future<Value> {
	private final Key key;
	private final WeakReference<FutureCallback<Value>> callbackRef;
	private boolean cancelled = false;
	private boolean done = false;
	private Value value;
	private Throwable exception;

	public FutureTask(Key key, FutureCallback<Value> callback) {
		this.key = key;
		this.callbackRef = new WeakReference<>(callback);
	}

	public Key getKey() {
		return key;
	}

	public boolean cancel() {
		return cancel(false);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (this) {
			cancelled = true;
			exception = new CancellationException();
			((Object) this).notifyAll();
		}

		FutureCallback<Value> callback = callbackRef.get();
		if (callback != null) {
			callback.onFailure(exception);
		}

		return true;
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
	public synchronized Value get() throws InterruptedException, ExecutionException {
		while (!done && !cancelled) {
			((Object)this).wait();
		}
		return report();
	}

	@Override
	public synchronized Value get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long deadLine = unit.toMillis(timeout);

		while (!done && !cancelled && deadLine > System.currentTimeMillis()) {
			((Object)this).wait(deadLine - System.currentTimeMillis());
		}

		if (deadLine <= System.currentTimeMillis() && !done) {
			throw new TimeoutException();
		}

		return report();
	}

	private Value report() throws ExecutionException {
		if (cancelled)
			throw new CancellationException();
		if (exception != null)
			throw new ExecutionException(exception);

		return value;
	}

	public void publish(Throwable exception, Value value) {
		synchronized (this) {
			this.exception = exception;
			this.done = true;
			this.value = value;
			((Object)this).notifyAll();
		}

		FutureCallback<Value> callback = callbackRef.get();
		if (callback != null && !cancelled) {
			if (exception != null) {
				callback.onFailure(exception);
			} else {
				callback.onCompleted(value);
			}
		}
	}
}
