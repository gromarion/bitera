package com.app.bitera.service;

import com.app.bitera.model.Receipt;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

@Service
public class TransactionService {

  private final Web3j web3j;
  private final Credentials credentials;
  private final String account;

  private static final String CREDENTIALS_FILE_NAME = "credentials.properties";
  private static final String NETWORK_ADDRESS_PROPERTY_NAME = "networkAddress";
  private static final String PRIVATE_KEY_PROPERTY_NAME = "privateKey";
  private static final String ACCOUNT_PROPERTY_NAME = "account";
  private static final String ETHERSCAN_BASE_URL = "https://ropsten.etherscan.io/address/%s";
  private static final BigDecimal ETH = BigDecimal.valueOf(0.01);
  private static final BigInteger WEI = Convert.toWei(ETH, Unit.ETHER).toBigIntegerExact();
  private static final BigInteger GAS_LIMIT = BigInteger.valueOf(21000L);

  @SneakyThrows
  public TransactionService() {
    InputStream inputStream = getClass()
        .getClassLoader()
        .getResourceAsStream(CREDENTIALS_FILE_NAME);
    if (inputStream != null) {
      Properties properties = new Properties();
      properties.load(inputStream);
      this.credentials = Credentials.create(properties.getProperty(PRIVATE_KEY_PROPERTY_NAME));
      this.web3j = Web3j.build(
          new HttpService(properties.getProperty(NETWORK_ADDRESS_PROPERTY_NAME)));
      this.account = properties.getProperty(ACCOUNT_PROPERTY_NAME);
    } else {
      throw new FileNotFoundException(
          String.format("Credentials file '%s' not found in classpath", CREDENTIALS_FILE_NAME));
    }
  }

  @SneakyThrows
  public Receipt sendEth(String address) {
    RawTransaction transaction = getTransaction(address);
    byte[] signedMessage = TransactionEncoder.signMessage(transaction, credentials);
    String signedTransactionData = Numeric.toHexString(signedMessage);
    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedTransactionData)
        .send();
    String transactionHash = ethSendTransaction.getTransactionHash();
    web3j.ethGetTransactionReceipt(transactionHash).sendAsync();

    return Receipt.builder()
        .from(account)
        .to(address)
        .value(ETH)
        .transactionId(transactionHash)
        .etherScanAddress(String.format(ETHERSCAN_BASE_URL, address))
        .build();
  }

  @SneakyThrows
  private RawTransaction getTransaction(String address) {
    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
        account, DefaultBlockParameterName.PENDING).send();

    return RawTransaction.createEtherTransaction(
        ethGetTransactionCount.getTransactionCount(),
        web3j.ethGasPrice().send().getGasPrice(),
        GAS_LIMIT,
        address,
        WEI);
  }
}
