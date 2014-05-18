package com.arcao.wmt.ui.task;

import android.os.AsyncTask;

/**
 * Created by msloup on 18.5.2014.
 */
public interface Task {
	boolean cancel(boolean mayInterruptIfRunning);
	boolean isCancelled();
	AsyncTask.Status getStatus();
}
