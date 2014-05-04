package com.arcao.utils.concurrent;

public interface FutureCallback<Value> {
	void onCompleted(Value value);
	void onFailure(Throwable t);
}
