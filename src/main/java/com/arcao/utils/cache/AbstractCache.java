package com.arcao.utils.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin on 21. 4. 2014.
 */
public abstract class AbstractCache<Key, Value> implements Cache<Key, Value> {
	@Override
	public void put(Value value) {
		put(getKey(value), value);
	}

	@Override
	public void putAll(Iterable<Value> values) {
		Map<Key, Value> map = new HashMap<>();

		for (Value value : values) {
			map.put(getKey(value), value);
		}

		putAll(map);
	}
}
