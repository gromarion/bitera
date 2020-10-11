package com.app.bitera.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Receipt {

  private String from;
  private String to;
  private String transactionId;
  private BigDecimal value;
  private String etherScanAddress;
}
