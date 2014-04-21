package com.arcao.utils.cache;

public interface Cache<Key, Value> {
	Value get(Key key);
	void put(Key key, Value value);
	void put(Value value);
	Key getKey(Value value);
	boolean contains(Key key);
	int length();
	void clear();
}
