package com.arcao.wmt.data.services.geocache;

import android.content.Context;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.utils.cache.AbstractCache;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import timber.log.Timber;

public class GeocacheFileCache extends AbstractCache<String, SimpleGeocache> {
	protected final File baseDir;

	public GeocacheFileCache(Context context) {
		baseDir = new File(context.getCacheDir(), "geocaches");
		baseDir.mkdirs();
	}

	@Override
	public int length() {
		return baseDir.list().length;
	}

	@Override
	public void clear() {
		try {
			FileUtils.cleanDirectory(baseDir);
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
		}
	}

	@Override
	public boolean contains(String key) {
		return new File(baseDir, key).exists();
	}

	@Override
	public SimpleGeocache get(String key) {
		File f = new File(baseDir, key);

		if (!f.exists())
			return null;

		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(new FileInputStream(f));
			return Marshalling.SIMPLE_GEOCACHE.from(ois);
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return null;
		} finally {
			IOUtils.closeQuietly(ois);
		}
	}

	@Override
	public String getKey(SimpleGeocache value) {
		return value.getCacheCode();
	}

	@Override
	public void put(String key, SimpleGeocache value) {
		File f = new File(baseDir, value.getCacheCode());

		File tempF = null;
		ObjectOutputStream oos = null;
		try {
			tempF = File.createTempFile(value.getCacheCode(), "tmp");
			oos = new ObjectOutputStream(new FileOutputStream(tempF));
			Marshalling.SIMPLE_GEOCACHE.to(oos, value);
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return;
		} finally {
			IOUtils.closeQuietly(oos);
		}

		tempF.renameTo(f);
	}
}