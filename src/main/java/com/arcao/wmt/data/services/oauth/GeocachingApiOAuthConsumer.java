package com.arcao.wmt.data.services.oauth;

import com.arcao.geocaching.api.configuration.OAuthGeocachingApiConfiguration;

import oauth.signpost.basic.DefaultOAuthConsumer;

public class GeocachingApiOAuthConsumer extends DefaultOAuthConsumer {
	public GeocachingApiOAuthConsumer(OAuthGeocachingApiConfiguration configuration) {
		super(configuration.getConsumerKey(), configuration.getConsumerSecret());
	}
}
