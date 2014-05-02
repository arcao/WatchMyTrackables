package com.arcao.wmt.data.services.oauth;

import com.arcao.geocaching.api.configuration.OAuthGeocachingApiConfiguration;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.http.HttpRequest;

public class GeocachingApiOAuthProvider extends DefaultOAuthProvider {
	protected OkHttpClient client;

	public GeocachingApiOAuthProvider(OAuthGeocachingApiConfiguration configuration, OkHttpClient client) {
		super(configuration.getOAuthRequestUrl(), configuration.getOAuthAccessUrl(), configuration.getOAuthAuthorizeUrl());
		this.client = client;
	}

	@Override
	protected HttpRequest createRequest(String endpointUrl) throws IOException {
		HttpURLConnection connection = client.open(new URL(endpointUrl));
		connection.setRequestMethod("POST");
		connection.setAllowUserInteraction(false);
		connection.setRequestProperty("Content-Length", "0");
		return new HttpURLConnectionRequestAdapter(connection);
	}
}
