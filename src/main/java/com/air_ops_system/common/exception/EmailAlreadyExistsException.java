package com.air_ops_system.common.exception;

public class EmailAlreadyExistsException extends RuntimeException {
  
  public EmailAlreadyExistsException() {
    super("Email já cadastrado");
  }
}
