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

  private static final String CREDENTIALS_FILE_NAME = "credentials.properties";
  private static final String NETWORK_ADDRESS_PROPERTY_NAME = "networkAddress";
  private static final String PRIVATE_KEY_PROPERTY_NAME = "BITERA_EXAMPLE_APP_PRIVATE_KEY";
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
      this.credentials = Credentials.create(System.getenv(PRIVATE_KEY_PROPERTY_NAME));
      this.web3j = Web3j.build(
          new HttpService(properties.getProperty(NETWORK_ADDRESS_PROPERTY_NAME)));
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
    Receipt.ReceiptBuilder receiptBuilder = Receipt.builder()
        .from(credentials.getAddress())
        .to(address)
        .value(ETH)
        .etherScanAddress(String.format(ETHERSCAN_BASE_URL, address));

    if (ethSendTransaction.hasError()) {
      return receiptBuilder.errorMessage(ethSendTransaction.getError().getMessage())
          .build();
    }
    return receiptBuilder
        .transactionId(ethSendTransaction.getTransactionHash())
        .build();
  }

  @SneakyThrows
  private RawTransaction getTransaction(String address) {
    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
        credentials.getAddress(), DefaultBlockParameterName.PENDING).send();

    return RawTransaction.createEtherTransaction(
        ethGetTransactionCount.getTransactionCount(),
        web3j.ethGasPrice().send().getGasPrice(),
        GAS_LIMIT,
        address,
        WEI);
  }
}
