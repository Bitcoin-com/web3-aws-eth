package com.bitcoin.eth.web3;

import java.net.URI;
import java.util.Map;

import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;

import com.bitcoin.eth.signer.Aws4RequestSigner;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

public class AwsWeb3WssProvider extends WebSocketService {



	public AwsWeb3WssProvider(String url, boolean includeRawResponses, AwsCredentials awsCredentials, Region region) {
		super(createSocketClient(url, awsCredentials, region), includeRawResponses);
	}

	public AwsWeb3WssProvider(String url, AwsCredentials awsCredentials, Region region) {
		super(createSocketClient(url, awsCredentials, region), false);
	}

	private static WebSocketClient createSocketClient(String url, AwsCredentials awsCredentials, Region region) {
		return new WebSocketClient(URI.create(url), getHeaders(url, awsCredentials, region));
	}

	private static Map<String, String> getHeaders(String url, AwsCredentials awsCredentials, Region region) {
		return Aws4RequestSigner.getHeaders(url, "", awsCredentials, region, SdkHttpMethod.GET);
	}

}
