package com.arcao.utils.concurrent;

public interface Cancellable {
	/**
	 * Check whether this asynchronous operation has been cancelled.
	 * @return
	 */
	boolean isCancelled();

	/**
	 * Attempt to cancel this asynchronous operation.
	 * @return The return value is whether the operation cancelled successfully.
	 */
	boolean cancel();
}