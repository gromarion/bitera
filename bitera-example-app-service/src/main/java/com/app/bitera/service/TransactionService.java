package com.app.bitera.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert.Unit;

@Service
public class TransactionService {

  private final Web3j web3j;
  private final String privateKey;

  private static final String CREDENTIALS_FILE_NAME = "credentials.properties";
  private static final String NETWORK_ADDRESS_PROPERTY_NAME = "networkAddress";
  private static final String PRIVATE_KEY_PROPERTY_NAME = "privateKey";

  @SneakyThrows
  public TransactionService() {
    InputStream inputStream = getClass()
        .getClassLoader()
        .getResourceAsStream(CREDENTIALS_FILE_NAME);
    if (inputStream != null) {
      Properties properties = new Properties();
      properties.load(inputStream);
      this.privateKey = properties.getProperty(PRIVATE_KEY_PROPERTY_NAME);
      this.web3j = Web3j.build(
          new HttpService(properties.getProperty(NETWORK_ADDRESS_PROPERTY_NAME)));
    } else {
      throw new FileNotFoundException(
          String.format("Credentials file %s not found in classpath", CREDENTIALS_FILE_NAME));
    }
  }

  @SneakyThrows
  public TransactionReceipt sendEth(String address) {
    Credentials credentials = Credentials.create(privateKey);

    return Transfer
        .sendFunds(web3j, credentials, address, BigDecimal.valueOf(0.01), Unit.ETHER)
        .send();
  }
}
