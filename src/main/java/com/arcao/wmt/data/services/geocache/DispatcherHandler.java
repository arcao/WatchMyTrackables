package com.arcao.wmt.data.services.geocache;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.utils.concurrent.FutureTask;

public class DispatcherHandler extends Handler {
	public static final int GET_GEOCACHE_COMPLETE = 1;

	public DispatcherHandler(Looper looper) {
		super(looper);
	}

	public void dispatchResult(FutureTask<String, SimpleGeocache> task, SimpleGeocache data, Throwable t) {
		obtainMessage(GET_GEOCACHE_COMPLETE, new Result(task, data, t)).sendToTarget();
	}

	@Override
	public void handleMessage(final Message msg) {
		switch (msg.what) {
			case GET_GEOCACHE_COMPLETE:
				Result result = (Result) msg.obj;
				result.task.publish(result.exception, result.data);
				break;
			default:
				super.handleMessage(msg);
		}
	}

	private static class Result {
		protected final FutureTask<String, SimpleGeocache> task;
		protected final SimpleGeocache data;
		protected final Throwable exception;

		private Result(FutureTask<String, SimpleGeocache> task, SimpleGeocache data, Throwable exception) {
			this.task = task;
			this.data = data;
			this.exception = exception;
		}
	}
}
