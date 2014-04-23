package com.arcao.wmt.data.services.geocache;

import com.arcao.geocaching.api.data.SimpleGeocache;

import java.lang.ref.WeakReference;

public class GetGeocacheRequest {
	private final String cacheCode;
	private final WeakReference<GetGeocacheTarget> targetRef;
	private boolean cancelled = false;

	public GetGeocacheRequest(String cacheCode, GetGeocacheTarget target) {
		this.cacheCode = cacheCode;
		this.targetRef = new WeakReference<>(target);
	}

	public void cancel() {
		cancelled = true;
	}

	public String getCacheCode() {
		return cacheCode;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void publish(SimpleGeocache result) {
		GetGeocacheTarget target = targetRef.get();
		if (target != null && !cancelled) {
			target.onGetGeocache(result);
		}
	}

}
