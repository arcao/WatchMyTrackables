package com.arcao.geocaching.api.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.arcao.geocaching.api.data.DeviceInfo;
import com.arcao.wmt.App;

import timber.log.Timber;

public final class DeviceInfoFactory {
	public static DeviceInfo create(App app) {
		return new DeviceInfo(
				0,
				0,
				getVersion(app),
				Build.MANUFACTURER,
				Build.MODEL,
				Build.VERSION.RELEASE,
				0,
				app.getDeviceId(),
				null,
				null
		);
	}

	private static String getVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Timber.e(e, e.getMessage());
			return "1.0";
		}
	}

}
