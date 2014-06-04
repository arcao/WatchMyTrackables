package com.arcao.wmt.ui.task.iface;

import android.content.Intent;

public abstract interface FinishableTask extends Task {
	public abstract FinishableTask setOnFinishedListener(OnFinishedListener paramOnFinishedListener);

	public static abstract interface OnFinishedListener
	{
		public abstract void onFinished(Intent paramIntent);
	}
}
