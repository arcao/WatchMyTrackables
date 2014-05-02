package com.arcao.wmt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.arcao.wmt.App;
import com.arcao.wmt.R;
import com.arcao.wmt.data.services.account.AccountService;
import com.arcao.wmt.ui.fragment.dialog.OAuthLoginDialogFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WelcomeActivity extends FragmentActivity implements OAuthLoginDialogFragment.OnTaskFinishedListener {
	@InjectView(R.id.buttonSign)
	Button buttonSign;

	@Inject
	AccountService accountService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		App.get(this).inject(this);
		setContentView(R.layout.activity_welcome);
		ButterKnife.inject(this);

		buttonSign.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OAuthLoginDialogFragment.newInstance().show(getSupportFragmentManager(), "login");
			}
		});

		setResult(RESULT_CANCELED);
	}

	@Override
	public void onTaskFinished(Intent errorIntent) {
		if (errorIntent == null) {
			if (accountService.hasAccount()) {
				// TODO initial import
				setResult(RESULT_OK);
				finish();
			}
		} else {
			// TODO show error
		}
	}
}
