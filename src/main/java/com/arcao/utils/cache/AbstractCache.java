package com.arcao.utils.cache;

/**
 * Created by Martin on 21. 4. 2014.
 */
public abstract class AbstractCache<Key, Value> implements Cache<Key, Value> {
	@Override
	public void put(Value value) {
		put(getKey(value), value);
	}
}
