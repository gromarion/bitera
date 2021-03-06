package com.app.bitera.controller;

import com.app.bitera.exception.InvalidAddressException;
import com.app.bitera.model.Receipt;
import com.app.bitera.model.Transaction;
import com.app.bitera.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.WalletUtils;

@RestController
@RequestMapping("/transaction/v1")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/send")
  @ResponseStatus(HttpStatus.OK)
  public Receipt sendEthToAddress(@RequestBody Transaction transaction) {
    if (transaction.getAddress() == null || !WalletUtils.isValidAddress(transaction.address)) {
      throw new InvalidAddressException(transaction.getAddress());
    }
    return transactionService.sendEth(transaction.getAddress());
  }
}
