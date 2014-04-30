package com.arcao.geocaching.api.util;

import android.os.Build;
import com.arcao.geocaching.api.data.DeviceInfo;
import com.arcao.wmt.App;
import com.arcao.wmt.BuildConfig;

public final class DeviceInfoFactory {
	public static DeviceInfo create(App app) {
		return new DeviceInfo(
				0,
				0,
				BuildConfig.VERSION_NAME,
				Build.MANUFACTURER,
				Build.MODEL,
				Build.VERSION.RELEASE,
				0,
				app.getDeviceId(),
				null,
				null
		);
	}
}
