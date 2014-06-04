package com.arcao.wmt.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.arcao.wmt.R;
import com.arcao.wmt.ui.task.AddFavoritedTrackableTask;
import com.arcao.wmt.ui.task.iface.FinishableTask;

import javax.inject.Inject;
import javax.inject.Provider;

public class AddFavoritedTrackableDialogFragment extends AbstractDialogFragment implements FinishableTask.OnFinishedListener {
	private static final String STATE_INPUT = "INPUT";

	@InjectView(R.id.edit_text)
	EditText editText;

	private Button positiveButton;
	private FinishableTask task;

	@Inject
	Provider<AddFavoritedTrackableTask> taskProvider;

	public static AddFavoritedTrackableDialogFragment newInstance() {
		return new AddFavoritedTrackableDialogFragment();
	}

	public void onCancel(DialogInterface dialog) {
		if (this.task != null) {
			this.task.cancel(true);
		}
		super.onCancel(dialog);
	}

	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_trackable_add, null);
		ButterKnife.inject(this, view);

		if (savedInstanceState != null) {
			this.editText.setText(savedInstanceState.getCharSequence(STATE_INPUT));
		}

		return new AlertDialog.Builder(getActivity())
						.setTitle(R.string.trackable_add)
						.setView(view)
						.setPositiveButton(R.string.button_ok, new OnClickListenerAdapter())
						.create();
	}

	public void onFinished(Intent intent) {
		task = null;

		if (intent == null) {
			dismiss();
		} else {
			positiveButton.setEnabled(true);
			positiveButton.setText(R.string.button_ok);
		}
	}

	public void onPositiveButtonClick() {
		if ((editText.getText() == null) || (editText.getText().length() == 0)) {
			return;
		}

		String trackableCode = this.editText.getText().toString().toUpperCase();
		positiveButton.setEnabled(false);
		positiveButton.setText(getActivity().getResources().getString(R.string.trackable_retrieving, trackableCode));

		task = (FinishableTask) taskProvider.get().execute(trackableCode);
		task.setOnFinishedListener(this);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if ((this.editText != null) && (isShowing())) {
			outState.putCharSequence(STATE_INPUT, this.editText.getText());
		}
	}

	public void onStart() {
		super.onStart();

		AlertDialog dialog = (AlertDialog)getDialog();
		if (dialog != null) {
			positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					AddFavoritedTrackableDialogFragment.this.onPositiveButtonClick();
				}
			});
		}
	}
}
