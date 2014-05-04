package com.arcao.utils.concurrent;

public abstract class FutureCallbackAdapter<Value> implements FutureCallback<Value> {
	@Override
	public void onCompleted(Value value) { }

	@Override
	public void onFailure(Throwable t) { }
}
