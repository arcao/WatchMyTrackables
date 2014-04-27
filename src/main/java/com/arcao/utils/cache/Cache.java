package com.arcao.utils.cache;

import java.util.Map;

public interface Cache<Key, Value> {
	Value get(Key key);
	void put(Key key, Value value);
	void put(Value value);
	void putAll(Iterable<Value> values);
	void putAll(Map<Key, Value> values);
	Key getKey(Value value);
	boolean contains(Key key);
	int length();
	void clear();
}
