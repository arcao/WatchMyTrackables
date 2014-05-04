package com.arcao.utils.concurrent;

public interface Future<T> extends Cancellable, java.util.concurrent.Future<T> {
}