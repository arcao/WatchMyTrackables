package com.arcao.wmt.data.services.account;

import android.accounts.Account;
import android.content.SharedPreferences;

import com.arcao.wmt.constant.PrefConstants;

public class SharedPreferencesAccountService implements AccountService {
	protected SharedPreferences prefs;

	public SharedPreferencesAccountService(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public boolean hasAccount() {
		return prefs.getString(PrefConstants.USERNAME, null) != null;
	}

	@Override
	public boolean addAccount(Account account) {
		if (account == null || hasAccount())
			return false;

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PrefConstants.USERNAME, account.name);
		editor.remove(PrefConstants.ACCESS_TOKEN);
		editor.apply();

		return true;

	}

	@Override
	public Account getAccount() {
		String username = prefs.getString(PrefConstants.USERNAME, null);

		if (username == null)
			return null;

		return new Account(username, ACCOUNT_TYPE);

	}

	@Override
	public void removeAccount() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PrefConstants.USERNAME);
		editor.remove(PrefConstants.ACCESS_TOKEN);
		editor.apply();
	}

	@Override
	public String getAuthToken() {
		return hasAccount() ? prefs.getString(PrefConstants.ACCESS_TOKEN, null) : null;
	}

	@Override
	public boolean setAuthToken(String token) {
		if (!hasAccount())
			return false;

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PrefConstants.ACCESS_TOKEN, token);
		editor.apply();

		return true;
	}

	@Override
	public boolean invalidateAuthToken() {
		if (!hasAccount())
			return false;

		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PrefConstants.ACCESS_TOKEN);
		editor.apply();

		return true;
	}
}
