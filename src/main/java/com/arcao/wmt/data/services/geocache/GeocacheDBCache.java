package com.arcao.wmt.data.services.geocache;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.utils.cache.AbstractCache;
import com.arcao.wmt.data.database.model.GeocacheModel;

import java.util.Map;

/**
 * Created by msloup on 25.4.2014.
 */
public class GeocacheDBCache extends AbstractCache<String, SimpleGeocache> {

	@Override
	public SimpleGeocache get(String key) {
		GeocacheModel row = new Select().from(GeocacheModel.class).where("name = ?", key).executeSingle();
		return row != null ? row.data : null;
	}

	@Override
	public void put(String key, SimpleGeocache value) {
		new GeocacheModel(key, value).save();
	}

	@Override
	public void putAll(Map<String, SimpleGeocache> values) {
		ActiveAndroid.beginTransaction();
		try {
			for (Map.Entry<String, SimpleGeocache> entry : values.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}

			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}

	@Override
	public String getKey(SimpleGeocache key) {
		return key.getCacheCode();
	}

	@Override
	public boolean contains(String key) {
		return new Select().from(GeocacheModel.class).where("name = ?", key).count() > 0;
	}

	@Override
	public int length() {
		return new Select().from(GeocacheModel.class).count();
	}

	@Override
	public void clear() {
		new Delete().from(GeocacheModel.class).execute();
	}
}
