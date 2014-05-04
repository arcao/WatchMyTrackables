package com.arcao.utils.concurrent;

public interface FutureCallback<Value> {
	void onCompleted(Throwable e, Value value);
}
