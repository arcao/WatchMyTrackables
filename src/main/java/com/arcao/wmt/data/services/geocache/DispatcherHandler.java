package com.arcao.wmt.data.services.geocache;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.arcao.geocaching.api.data.SimpleGeocache;

public class DispatcherHandler extends Handler {
	public static final int GET_GEOCACHE_COMPLETE = 1;

	public DispatcherHandler(Looper looper) {
		super(looper);
	}

	public void dispatchGetGeocache(GetGeocacheRequest request, SimpleGeocache data, Throwable t) {
		obtainMessage(GET_GEOCACHE_COMPLETE, new GetGeocacheResult(request, data, t)).sendToTarget();
	}

	@Override
	public void handleMessage(final Message msg) {
		switch (msg.what) {
			case GET_GEOCACHE_COMPLETE:
				GetGeocacheResult result = (GetGeocacheResult) msg.obj;
				result.request.publish(result.data, result.t);
				break;
			default:
				super.handleMessage(msg);
		}
	}

	private static class GetGeocacheResult {
		protected final GetGeocacheRequest request;
		protected final SimpleGeocache data;
		protected final Throwable t;

		private GetGeocacheResult(GetGeocacheRequest request, SimpleGeocache data, Throwable t) {
			this.request = request;
			this.data = data;
			this.t = t;
		}
	}
}
