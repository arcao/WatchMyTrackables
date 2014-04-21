package com.arcao.geocaching.api.configuration.impl;

import com.arcao.geocaching.api.configuration.OAuthGeocachingApiConfiguration;
import com.arcao.wmt.BuildConfig;

public class ProductionGeocachingApiConfigurationImpl extends DefaultProductionGeocachingApiConfiguration implements OAuthGeocachingApiConfiguration {
	private static final String OAUTH_URL = "https://www.geocaching.com/oauth/mobileoauth.ashx";

	private static final String CONSUMER_KEY = BuildConfig.GEOCACHING_API_KEY;
	private static final String CONSUMER_SECRET = BuildConfig.GEOCACHING_API_SECRET;

	public String getConsumerKey() {
		return CONSUMER_KEY;
	}

	public String getConsumerSecret() {
		return CONSUMER_SECRET;
	}

	public String getOAuthRequestUrl() {
		return OAUTH_URL;
	}

	public String getOAuthAuthorizeUrl() {
		return OAUTH_URL;
	}

	public String getOAuthAccessUrl() {
		return OAUTH_URL;
	}
}
