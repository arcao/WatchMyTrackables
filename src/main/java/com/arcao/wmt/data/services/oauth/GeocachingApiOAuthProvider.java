package com.arcao.wmt.data.services.oauth;

import com.arcao.geocaching.api.configuration.OAuthGeocachingApiConfiguration;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.http.HttpRequest;

public class GeocachingApiOAuthProvider extends DefaultOAuthProvider {
	protected OkUrlFactory factory;

	public GeocachingApiOAuthProvider(OAuthGeocachingApiConfiguration configuration, OkHttpClient client) {
		super(configuration.getOAuthRequestUrl(), configuration.getOAuthAccessUrl(), configuration.getOAuthAuthorizeUrl());

		factory = new OkUrlFactory(client);
	}

	@Override
	protected HttpRequest createRequest(String endpointUrl) throws IOException {
		HttpURLConnection connection = factory.open(new URL(endpointUrl));
		connection.setRequestMethod("POST");
		connection.setAllowUserInteraction(false);
		connection.setRequestProperty("Content-Length", "0");
		return new HttpURLConnectionRequestAdapter(connection);
	}
}
