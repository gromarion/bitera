package com.app.bitera.advice;

import com.app.bitera.exception.InvalidAddressException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidAddressAdvice {

  @ResponseBody
  @ExceptionHandler(InvalidAddressException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String invalidAddressHandler(InvalidAddressException ex) {
    return ex.getMessage();
  }
}
