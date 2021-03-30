package com.bitcoin.eth.interceptor;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.bitcoin.eth.signer.Aws4RequestSigner;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

public class Aws4SignerInterceptor implements Interceptor {

	private final AwsCredentials awsCredentials;
	private final Region region;

	public Aws4SignerInterceptor(AwsCredentials awsCredentials, Region region) {
		this.awsCredentials = Objects.requireNonNull(awsCredentials);
		this.region = Objects.requireNonNull(region);
	}

	@NotNull
	@Override
	public Response intercept(@NotNull Chain chain) throws IOException {
		Request request = chain.request();
		String bodyToString = bodyToString(request.body());
		URL url = request.url().url();
		Map<String, String> headers = Aws4RequestSigner.getHeaders(url.toString(), bodyToString, awsCredentials, region, SdkHttpMethod.POST);
		Builder signedRequestOkhttpBuilder = request.newBuilder()
				.url(url);
		headers.forEach(signedRequestOkhttpBuilder::addHeader);
		return chain.proceed(signedRequestOkhttpBuilder.build());
	}

	private static String bodyToString(@Nullable RequestBody request) {
		return Optional.ofNullable(request).map(req -> {
			try (Buffer buffer = new Buffer()) {
				request.writeTo(buffer);
				return buffer.readUtf8();
			} catch (Exception e) {
				return "";
			}
		}).orElse("");
	}
}
