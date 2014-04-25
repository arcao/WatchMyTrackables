package com.arcao.geocaching.api.downloader;

import com.arcao.geocaching.api.configuration.GeocachingApiConfiguration;
import com.arcao.geocaching.api.exception.InvalidResponseException;
import com.arcao.geocaching.api.exception.NetworkException;
import com.arcao.geocaching.api.impl.live_geocaching_api.downloader.JsonDownloader;
import com.arcao.geocaching.api.impl.live_geocaching_api.parser.JsonReader;
import com.arcao.wmt.BuildConfig;
import com.squareup.okhttp.OkHttpClient;
import timber.log.Timber;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by msloup on 24.4.2014.
 */
public class OkHttpClientJsonDownloader implements JsonDownloader {
	protected OkHttpClient client;
	protected GeocachingApiConfiguration configuration;

	public OkHttpClientJsonDownloader(GeocachingApiConfiguration configuration, OkHttpClient client) {
		this.configuration = configuration;
		this.client = client;
	}

	@Override
	public JsonReader get(URL url) throws NetworkException, InvalidResponseException {
		InputStream is;
		InputStreamReader isr;

		try {
			HttpURLConnection con = client.open(url);

			// important! sometimes GC API takes too long to return response
			con.setConnectTimeout(configuration.getConnectTimeout());
			con.setReadTimeout(configuration.getReadTimeout());

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Watch My Trackables " + BuildConfig.VERSION_NAME);
			con.setRequestProperty("Accept", "application/json");

			if (con.getResponseCode() >= 400) {
				is = con.getErrorStream();
			} else {
				is = con.getInputStream();
			}

			if (con.getResponseCode() >= 400) {
				isr = new InputStreamReader(is, "UTF-8");

				StringBuilder sb = new StringBuilder();
				char buffer[] = new char[1024];

				int len;
				while ((len = isr.read(buffer)) != -1) {
					sb.append(buffer, 0, len);
				}

				isr.close();

				// read error response
				throw new InvalidResponseException(sb.toString());
			}

			isr = new InputStreamReader(is, "UTF-8");
			return new JsonReader(isr);
		} catch (InvalidResponseException e) {
			throw e;
		} catch (Exception e) {
			Timber.e(e, e.getMessage());
			throw new NetworkException("Error while downloading data (" + e.getClass().getSimpleName() + ")", e);
		}
	}

	@Override
	public JsonReader post(URL url, byte[] postData) throws NetworkException, InvalidResponseException {
		InputStream is;
		InputStreamReader isr;

		try {
			HttpURLConnection con = client.open(url);

			con.setDoOutput(true);

			// important! sometimes GC API takes too long to return response
			con.setConnectTimeout(configuration.getConnectTimeout());
			con.setReadTimeout(configuration.getReadTimeout());

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Content-Length", Integer.toString(postData.length));
			con.setRequestProperty("User-Agent", "Watch My Trackables " + BuildConfig.VERSION_NAME);
			con.setRequestProperty("Accept", "application/json");

			OutputStream os = con.getOutputStream();

			os.write(postData);
			os.flush();
			os.close();

			if (con.getResponseCode() >= 400) {
				is = con.getErrorStream();
			} else {
				is = con.getInputStream();
			}

			if (con.getResponseCode() >= 400) {
				isr = new InputStreamReader(is, "UTF-8");

				StringBuilder sb = new StringBuilder();
				char buffer[] = new char[1024];

				int len;
				while ((len = isr.read(buffer)) != -1) {
					sb.append(buffer, 0, len);
				}

				isr.close();

				// read error response
				throw new InvalidResponseException(sb.toString());
			}

			isr = new InputStreamReader(is, "UTF-8");

			return new JsonReader(isr);
		} catch (InvalidResponseException e) {
			throw e;
		} catch (Exception e) {
			Timber.e(e, e.getMessage());
			throw new NetworkException("Error while downloading data (" + e.getClass().getSimpleName() + "): " + e.getMessage(), e);
		}
	}
}
