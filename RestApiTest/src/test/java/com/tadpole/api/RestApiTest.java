package com.tadpole.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestApiTest {
	private static String TEST_SERVER_FIRST_PREFIX = "http://127.0.0.1:8080/api/";
	private static String ACCESS_KEY = "87c83d56-47d2-41a1-a599-9a22ed2470dd";
	private static String SECRET_KEY = "7291299a-0fa8-4c87-ab0a-cc414b63c625";
	
	private CloseableHttpClient httpClient;

	@Before
	public void setUp() {
		httpClient = HttpClients.createDefault();
	}

	@After
	public void shutDown() throws IOException {
		httpClient.close();
	}

	@Test
	public void normalRequest() throws ClientProtocolException, IOException {
		String uri = TEST_SERVER_FIRST_PREFIX + "elasticsearch/size?size=10&resultType=json";
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("TDB_ACCESS_KEY", ACCESS_KEY);
		request.addHeader("TDB_SECRET_KEY", SECRET_KEY);

		HttpResponse httpResponse = httpClient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(200, statusCode);

		BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

		// convert json's string to json's object
		String line;
		StringBuilder output = new StringBuilder();
		while ((line = br.readLine()) != null) {
			output.append(line);
		}
		
		JSONObject obj = (JSONObject) JSONValue.parse(output.toString());
		assertEquals(false, obj.get("timed_out"));
//		assertEquals("For Those About To Rock We Salute You", obj.get("title"));
	}

	@Test
	public void invalidKey() throws ClientProtocolException, IOException {
		String uri = TEST_SERVER_FIRST_PREFIX + "elasticsearch/size?size=10&resultType=json";
		HttpUriRequest request = new HttpGet(uri);

		HttpResponse httpResponse = httpClient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(401, statusCode);

		request.addHeader("TDB_ACCESS_KEY", "xxxxxxxx");
		request.addHeader("TDB_SECRET_KEY", "xxxxxxxx");

		httpResponse = httpClient.execute(request);

		statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(401, statusCode);
	}

	@Test
	public void invalidUrl() throws ClientProtocolException, IOException {
		String uri = TEST_SERVER_FIRST_PREFIX + "rest/base/xxxxx";
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("TDB_ACCESS_KEY", ACCESS_KEY);
		request.addHeader("TDB_SECRET_KEY", SECRET_KEY);

		HttpResponse httpResponse = httpClient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(404, statusCode);
	}

}
