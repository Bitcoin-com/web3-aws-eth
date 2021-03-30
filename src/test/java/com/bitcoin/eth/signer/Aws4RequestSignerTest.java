package com.bitcoin.eth.signer;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

public class Aws4RequestSignerTest {

	private AwsCredentials awsCredentials;

	@Before
	public void setUp() {
		awsCredentials = Mockito.mock(AwsCredentials.class);
		Mockito.when(awsCredentials.accessKeyId()).thenReturn("AKIAIOSFODNN7EXAMPLE");
		Mockito.when(awsCredentials.secretAccessKey()).thenReturn("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
	}

	@Test
	public void getHeaders_whenGetRequestNoBody_shouldSign() {
		Map<String, String> headers = Aws4RequestSigner.getHeaders("http://mynode:443", "", awsCredentials, Region.AP_NORTHEAST_1, SdkHttpMethod.GET);
		assertThat(headers.get(Aws4RequestSigner.AUTHORIZATION_HEADER), Matchers.containsString("Signature="));
		assertThat(headers.get(Aws4RequestSigner.AMZ_DATE_HEADER), Matchers.notNullValue());
	}

	@Test
	public void getHeaders_whenGetRequestBody_shouldSign() {
		Map<String, String> headers = Aws4RequestSigner.getHeaders("http://mynode:443",
				"{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":0}",
				awsCredentials, Region.AP_NORTHEAST_1, SdkHttpMethod.GET);
		assertThat(headers.get(Aws4RequestSigner.AUTHORIZATION_HEADER), Matchers.containsString("Signature="));
		assertThat(headers.get(Aws4RequestSigner.AMZ_DATE_HEADER), Matchers.notNullValue());
	}

	@Test
	public void getHeaders_whenUrlWss_shouldSign() {
		Map<String, String> headers = Aws4RequestSigner.getHeaders("wss://mynode:443",
				"{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":0}",
				awsCredentials, Region.AP_NORTHEAST_1, SdkHttpMethod.GET);
		assertThat(headers.get(Aws4RequestSigner.AUTHORIZATION_HEADER), Matchers.containsString("Signature="));
		assertThat(headers.get(Aws4RequestSigner.AMZ_DATE_HEADER), Matchers.notNullValue());
	}
}
