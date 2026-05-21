package com.air_ops_system.common.exception;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException() {
    super("Email ou senha inválidos");
  }
}
