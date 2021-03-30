package com.bitcoin.eth.signer;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

public class Aws4RequestSigner {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AMZ_DATE_HEADER = "X-Amz-Date";

	public static Map<String, String> getHeaders(String url, String body, AwsCredentials awsCredentials, Region region, SdkHttpMethod method) {
		Aws4SignerParams signingParams = Aws4SignerParams.builder()
				.awsCredentials(awsCredentials)
				.signingRegion(region)
				.signingName("managedblockchain")
				.build();

		String parsedUrl = url.replaceFirst("wss", "https"); //AWS SDK Doesn't allow signing with wss format

		SdkHttpFullRequest preSignedRequest = SdkHttpFullRequest.builder()
				.contentStreamProvider(() -> new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)))
				.method(method)
				.host(url)
				.uri(URI.create(parsedUrl))
				.build();

		Aws4Signer aws4Signer = Aws4Signer.create();
		SdkHttpFullRequest signedRequest = aws4Signer.sign(preSignedRequest, signingParams);

		String authHeader = signedRequest.firstMatchingHeader(AUTHORIZATION_HEADER).orElseThrow(() -> new RuntimeException("Could not find amazon header=" + AUTHORIZATION_HEADER));
		String dateHeader = signedRequest.firstMatchingHeader(AMZ_DATE_HEADER).orElseThrow(() -> new RuntimeException("Could not find amazon header=" + AMZ_DATE_HEADER));
		return Map.of(AUTHORIZATION_HEADER, authHeader, AMZ_DATE_HEADER, dateHeader);
	}
}
