package com.arcao.wmt.data.services.geocache;

import android.content.Context;
import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.utils.cache.AbstractCache;
import com.arcao.wmt.data.marshalling.SimpleGeocacheMarshaller;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import timber.log.Timber;

import java.io.*;
import java.util.Map;

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
			return SimpleGeocacheMarshaller.INSTANCE.from(ois);
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
			SimpleGeocacheMarshaller.INSTANCE.to(oos, value);
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return;
		} finally {
			IOUtils.closeQuietly(oos);
		}

		tempF.renameTo(f);
	}

	@Override
	public void putAll(Map<String, SimpleGeocache> values) {
		for (Map.Entry<String, SimpleGeocache> entry : values.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}