package com.bitcoin.eth.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;

public class Aws4SignerInterceptorTest {

	private Aws4SignerInterceptor aws4SignerInterceptor;
	private AwsCredentials awsCredentials;
	private OkHttpClient okHttpClient;
	private MockWebServer mockWebServer;

	@Before
	public void setUp() throws Exception {
		awsCredentials = Mockito.mock(AwsCredentials.class);
		aws4SignerInterceptor = new Aws4SignerInterceptor(awsCredentials, Region.AP_NORTHEAST_1);
		okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(aws4SignerInterceptor)
				.build();
		mockWebServer = new MockWebServer();
	}

	@Test
	public void intercept_whenInterceptorRuns_shouldApplyAWSHeaders() throws Exception {
		Mockito.when(awsCredentials.accessKeyId()).thenReturn("AKIAIOSFODNN7EXAMPLE");
		Mockito.when(awsCredentials.secretAccessKey()).thenReturn("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
		MockResponse response = new MockResponse();
		response.setBody("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0xb8cf08\"}");
		mockWebServer.enqueue(response);

		RequestBody requestBody = RequestBody.create("{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":0}", MediaType.parse("application/json; charset=utf-8"));

		okhttp3.Request httpRequest =
				new okhttp3.Request.Builder().url(mockWebServer.url("/").toString()).post(requestBody).build();

		Response execute = okHttpClient.newCall(httpRequest).execute();

		RecordedRequest recordedRequest = mockWebServer.takeRequest();
		Headers headers = recordedRequest.getHeaders();


		assertThat(headers.get("Authorization"), Matchers.containsString("Signature="));
		assertThat(headers.get("X-Amz-Date"), Matchers.notNullValue());
	}

	@Test(expected = RuntimeException.class)
	public void intercept_whenInterceptorRunsNoCredentials_shouldFail() throws Exception {
		MockResponse response = new MockResponse();
		response.setBody("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0xb8cf08\"}");
		mockWebServer.enqueue(response);

		RequestBody requestBody = RequestBody.create("{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":0}", MediaType.parse("application/json; charset=utf-8"));

		okhttp3.Request httpRequest =
				new okhttp3.Request.Builder().url(mockWebServer.url("/").toString()).post(requestBody).build();

		Response execute = okHttpClient.newCall(httpRequest).execute();
	}

}
