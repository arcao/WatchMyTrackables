package com.arcao.wmt.data.services.account;

import android.accounts.Account;

import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.wmt.BuildConfig;

/**
 * Created by Martin on 2. 5. 2014.
 */
public interface AccountService {
	public static final String ACCOUNT_TYPE = BuildConfig.PACKAGE_NAME;

	boolean hasAccount();
	boolean addAccount(Account account);
	Account getAccount();
	void removeAccount();

	String getAuthToken();
	boolean setAuthToken(String token);
	boolean invalidateAuthToken();

	void apply(GeocachingApi api);
}
