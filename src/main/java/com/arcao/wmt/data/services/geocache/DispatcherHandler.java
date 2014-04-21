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

	public void dispatchGetGeocache(GetGeocacheRequest request, SimpleGeocache data) {
		obtainMessage(GET_GEOCACHE_COMPLETE, new GetGeocacheResult(request, data)).sendToTarget();
	}

	@Override
	public void handleMessage(final Message msg) {
		switch (msg.what) {
			case GET_GEOCACHE_COMPLETE:
				GetGeocacheResult result = (GetGeocacheResult) msg.obj;
				result.request.publish(result.data);
				break;
			default:
				super.handleMessage(msg);
		}
	}

	private static class GetGeocacheResult {
		protected final GetGeocacheRequest request;
		protected final SimpleGeocache data;

		private GetGeocacheResult(GetGeocacheRequest request, SimpleGeocache data) {
			this.request = request;
			this.data = data;
		}
	}
}
