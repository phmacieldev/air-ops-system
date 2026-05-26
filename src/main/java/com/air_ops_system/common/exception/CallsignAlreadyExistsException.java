package com.air_ops_system.common.exception;

public class CallsignAlreadyExistsException extends RuntimeException {
  public CallsignAlreadyExistsException() {
    super("Callsign já cadastrado");
  }
}
