package com.arcao.wmt.data.services.geocache;

import com.arcao.geocaching.api.data.SimpleGeocache;

import java.lang.ref.WeakReference;

public class GetGeocacheRequest {
	private final String cacheCode;
	private final WeakReference<GetGeocacheTarget> targetRef;

	public GetGeocacheRequest(String cacheCode, GetGeocacheTarget target) {
		this.cacheCode = cacheCode;
		this.targetRef = new WeakReference<>(target);
	}

	public String getCacheCode() {
		return cacheCode;
	}

	public void publish(SimpleGeocache result) {
		GetGeocacheTarget target = targetRef.get();
		if (target != null) {
			target.onGetGeocache(result);
		}
	}

}
