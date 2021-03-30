# Web3-aws-eth
Library for signing requests with AWS4Signer on a custom Web3 provider
## How to use

### HttpProvider
```java
Aws4SignerInterceptor aws4SignerInterceptor = new Aws4SignerInterceptor(AwsBasicCredentials.create("MY_KEY", "MY_SECRET"), Region.AP_NORTHEAST_1);
AwsWeb3HttpProvider provider = new AwsWeb3HttpProvider("https://my-node-id-lowercase.ethereum.managedblockchain.us-east-1.amazonaws.com/", aws4SignerInterceptor);
Web3j web3j = Web3j.build(provider);
```

### WebsocketProvider
```java
AwsWeb3WssProvider awsWeb3WssProvider = new AwsWeb3WssProvider("wss://my-node-id-lowercase.wss.ethereum.managedblockchain.us-east-1.amazonaws.com/", AwsBasicCredentials.create("MY_KEY", "MY_SECRET"), Region.AP_NORTHEAST_1);
awsWeb3WssProvider.connect();
Web3j web3j = Web3j.build(awsWeb3WssProvider);
```

## Inspired by
https://docs.aws.amazon.com/managed-blockchain/latest/ethereum-dev/ethereum-json-rpc.html

## Author
[Andreas Larsson](https://github.com/AndreasLarssons)
