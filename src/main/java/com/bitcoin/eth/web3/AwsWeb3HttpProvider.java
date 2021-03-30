package com.bitcoin.eth.web3;

import static okhttp3.ConnectionSpec.CLEARTEXT;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.http.HttpService;

import com.bitcoin.eth.interceptor.Aws4SignerInterceptor;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class AwsWeb3HttpProvider extends HttpService {

	private static final Logger logger = LoggerFactory.getLogger(AwsWeb3HttpProvider.class);

	/**
	 * Copied from {@link HttpService#INFURA_CIPHER_SUITES}.
	 */
	@SuppressWarnings("JavadocReference")
	private static final CipherSuite[] INFURA_CIPHER_SUITES =
			new CipherSuite[] {
					CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
					CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
					CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
					CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
					CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
					CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,

					// Note that the following cipher suites are all on HTTP/2's bad cipher suites list.
					// We'll
					// continue to include them until better suites are commonly available. For example,
					// none
					// of the better cipher suites listed above shipped with Android 4.4 or Java 7.
					CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
					CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
					CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
					CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
					CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
					CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
					CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
					CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
					CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA,

					// Additional INFURA CipherSuites
					CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
					CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
					CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
					CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256
			};

	private static final ConnectionSpec INFURA_CIPHER_SUITE_SPEC =
			new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
					.cipherSuites(INFURA_CIPHER_SUITES)
					.build();

	/**
	 * The list of {@link ConnectionSpec} instances used by the connection.
	 */
	private static final List<ConnectionSpec> CONNECTION_SPEC_LIST =
			Arrays.asList(INFURA_CIPHER_SUITE_SPEC, CLEARTEXT);

	public AwsWeb3HttpProvider(String url, boolean includeRawResponses, Aws4SignerInterceptor aws4SignerInterceptor) {
		super(url, createOkHttpClient(aws4SignerInterceptor), includeRawResponses);
	}

	public AwsWeb3HttpProvider(String url, Aws4SignerInterceptor aws4SignerInterceptor) {
		super(url, createOkHttpClient(aws4SignerInterceptor), false);
	}

	private static OkHttpClient createOkHttpClient(Aws4SignerInterceptor aws4SignerInterceptor) {
		final OkHttpClient.Builder builder =
				new OkHttpClient.Builder().connectionSpecs(CONNECTION_SPEC_LIST);
		configureLogging(builder);
		builder.addInterceptor(aws4SignerInterceptor);
		return builder.build();
	}

	private static void configureLogging(OkHttpClient.Builder builder) {
		if (logger.isDebugEnabled()) {
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor(logger::debug);
			logging.setLevel(HttpLoggingInterceptor.Level.BODY);
			builder.addInterceptor(logging);
		}
	}
}
