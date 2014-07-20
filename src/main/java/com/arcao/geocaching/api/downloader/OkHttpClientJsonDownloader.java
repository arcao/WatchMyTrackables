package com.arcao.geocaching.api.downloader;

import com.arcao.geocaching.api.configuration.GeocachingApiConfiguration;
import com.arcao.geocaching.api.exception.InvalidResponseException;
import com.arcao.geocaching.api.exception.NetworkException;
import com.arcao.geocaching.api.impl.live_geocaching_api.downloader.JsonDownloader;
import com.arcao.geocaching.api.impl.live_geocaching_api.parser.JsonReader;
import com.arcao.wmt.BuildConfig;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.Reader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class OkHttpClientJsonDownloader implements JsonDownloader {
	private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	protected OkHttpClient client;

	public OkHttpClientJsonDownloader(GeocachingApiConfiguration configuration, OkHttpClient client) {
		this.client = client.clone();

		this.client.setConnectTimeout(configuration.getConnectTimeout(), TimeUnit.MILLISECONDS);
		this.client.setReadTimeout(configuration.getReadTimeout(), TimeUnit.MILLISECONDS);
	}

	@Override
	public JsonReader get(URL url) throws NetworkException, InvalidResponseException {
		Reader reader;

		try {
			Request request = new Request.Builder()
							.url(url)
							.addHeader("User-Agent", "Watch My Trackables " + BuildConfig.VERSION_NAME)
							.addHeader("Accept", "application/json")
							.build();

			Response response = client.newCall(request).execute();

			ResponseBody body =  response.body();
			reader = body.charStream();

			if (!response.isSuccessful()) {
				StringBuilder sb = new StringBuilder();
				char buffer[] = new char[1024];

				int len;
				while ((len = reader.read(buffer)) != -1) {
					sb.append(buffer, 0, len);
				}

				reader.close();

				// read error response
				throw new InvalidResponseException(sb.toString());
			}

			return new JsonReader(reader);
		} catch (InvalidResponseException e) {
			throw e;
		} catch (Throwable e) {
			Timber.e(e, e.getMessage());
			throw new NetworkException("Error while downloading data (" + e.getClass().getSimpleName() + ")", e);
		}
	}

	@Override
	public JsonReader post(URL url, byte[] postData) throws NetworkException, InvalidResponseException {
		Reader reader;

		try {
			Request request = new Request.Builder()
							.url(url)
							.method("POST", RequestBody.create(MEDIA_TYPE_JSON, postData))
							.addHeader("User-Agent", "Watch My Trackables " + BuildConfig.VERSION_NAME)
							.addHeader("Accept", "application/json")
							.build();


			Response response = client.newCall(request).execute();

			ResponseBody body =  response.body();
			reader = body.charStream();

			if (!response.isSuccessful()) {
				StringBuilder sb = new StringBuilder();
				char buffer[] = new char[1024];

				int len;
				while ((len = reader.read(buffer)) != -1) {
					sb.append(buffer, 0, len);
				}

				reader.close();

				// read error response
				throw new InvalidResponseException(sb.toString());
			}

			return new JsonReader(reader);
		} catch (InvalidResponseException e) {
			throw e;
		} catch (Throwable e) {
			Timber.e(e, e.getMessage());
			throw new NetworkException("Error while downloading data (" + e.getClass().getSimpleName() + "): " + e.getMessage(), e);
		}
	}
}
