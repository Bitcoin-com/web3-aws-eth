package com.bitcoin.eth.web3;

import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigInteger;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;

import io.fabric8.mockwebserver.DefaultMockServer;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;

public class AwsWeb3WssProviderTest {

	private AwsWeb3WssProvider awsWeb3WssProvider;
	private DefaultMockServer server;

	@Before
	public void setUp() {
		server = new DefaultMockServer();
		AwsCredentials awsCredentials = Mockito.mock(AwsCredentials.class);
		Mockito.when(awsCredentials.accessKeyId()).thenReturn("AKIAIOSFODNN7EXAMPLE");
		Mockito.when(awsCredentials.secretAccessKey()).thenReturn("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
		awsWeb3WssProvider = new AwsWeb3WssProvider(server.url("/"), awsCredentials, Region.AP_NORTHEAST_1);
	}

	@After
	public void tearDown() {
		server.shutdown();
	}

	@Test
	public void awsWeb3WssProvider_whenSendBlocking_shouldParseCorrectly() throws Exception {
		server.expect().get().withPath("/")
				.andUpgradeToWebSocket()
				.open()
				.expect("{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":0}").andEmit("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0xb8cf08\"}").once()
				.done()
				.once();

		Web3j web3j = Web3j.build(awsWeb3WssProvider);
		awsWeb3WssProvider.connect();

		BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
		assertThat(blockNumber, Matchers.is(new BigInteger("12111624")));
	}

}
