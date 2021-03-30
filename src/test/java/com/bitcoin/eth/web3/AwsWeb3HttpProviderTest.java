package com.bitcoin.eth.web3;

import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigInteger;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import com.bitcoin.eth.interceptor.Aws4SignerInterceptor;

import okhttp3.Interceptor.Chain;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class AwsWeb3HttpProviderTest {

	private Aws4SignerInterceptor aws4SignerInterceptor;
	private AwsWeb3HttpProvider awsWeb3HttpProvider;
	private MockWebServer mockWebServer;

	@Before
	public void setUp() {
		aws4SignerInterceptor = Mockito.mock(Aws4SignerInterceptor.class);
		mockWebServer = new MockWebServer();
		awsWeb3HttpProvider = new AwsWeb3HttpProvider(mockWebServer.url("/").toString(), aws4SignerInterceptor);
	}

	@Test
	public void awsWeb3HttpProvider_whenSendBlocking_shouldParseCorrectly() throws Exception {
		Mockito.when(aws4SignerInterceptor.intercept(Mockito.any())).thenAnswer(invocationOnMock -> {
			Chain chain = invocationOnMock.getArgument(0);
			return chain.proceed(chain.request());
		});

		MockResponse response = new MockResponse();
		response.setBody("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0xb8cf08\"}");
		mockWebServer.enqueue(response);

		Web3j web3j = Web3j.build(awsWeb3HttpProvider);

		BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();

		assertThat(blockNumber, Matchers.is(new BigInteger("12111624")));
	}

	@Test
	public void awsWeb3HttpProvider_whenSendFlowable_shouldParseCorrectly() throws Exception {
		Mockito.when(aws4SignerInterceptor.intercept(Mockito.any())).thenAnswer(invocationOnMock -> {
			Chain chain = invocationOnMock.getArgument(0);
			return chain.proceed(chain.request());
		});

		MockResponse response = new MockResponse();
		response.setBody("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0xb8cf08\"}");
		mockWebServer.enqueue(response);

		Web3j web3j = Web3j.build(awsWeb3HttpProvider);

		BigInteger blockNumber = web3j.ethBlockNumber().flowable().map(EthBlockNumber::getBlockNumber).blockingFirst();
		assertThat(blockNumber, Matchers.is(new BigInteger("12111624")));
	}

	@Test
	public void awsWeb3HttpProvider_whenSendCompletableFuture_shouldParseCorrectly() throws Exception {
		Mockito.when(aws4SignerInterceptor.intercept(Mockito.any())).thenAnswer(invocationOnMock -> {
			Chain chain = invocationOnMock.getArgument(0);
			return chain.proceed(chain.request());
		});

		MockResponse response = new MockResponse();
		response.setBody("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0xb8cf08\"}");
		mockWebServer.enqueue(response);

		Web3j web3j = Web3j.build(awsWeb3HttpProvider);

		BigInteger blockNumber = web3j.ethBlockNumber().sendAsync().thenApply(EthBlockNumber::getBlockNumber).get();
		assertThat(blockNumber, Matchers.is(new BigInteger("12111624")));
	}

}
