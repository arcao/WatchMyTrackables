package com.arcao.wmt.ui.task;

import android.content.Intent;

/**
 * Created by msloup on 18.5.2014.
 */
public interface UpdateTrackablesTask extends Task {
	UpdateTrackablesTask setOnFinishedListener(OnFinishedListener listener);
	UpdateTrackablesTask execute();

	public interface OnFinishedListener {
		void onFinished(Intent errorIntent);
	}
}
