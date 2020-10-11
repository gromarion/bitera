package com.app.bitera.exception;

public class InvalidAddressException extends RuntimeException {

  public InvalidAddressException(String address) {
    super(String.format("Invalid destination address: %s", address));
  }
}
