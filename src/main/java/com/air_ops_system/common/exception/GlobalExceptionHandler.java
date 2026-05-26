package com.air_ops_system.common.exception;

import com.air_ops_system.common.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiErrorDTO> handleEmailAlreadyExists(
      EmailAlreadyExistsException exception,
      HttpServletRequest request
  ) {
    return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiErrorDTO> handleInvalidCredentials(
      InvalidCredentialsException exception,
      HttpServletRequest request
  ) {
    return buildError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
  }

  private ResponseEntity<ApiErrorDTO> buildError(
      HttpStatus status,
      String message,
      HttpServletRequest request
  ) {
    ApiErrorDTO error = new ApiErrorDTO(
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        LocalDateTime.now()
    );

    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiErrorDTO> handleRuntimeException(
      RuntimeException exception,
      HttpServletRequest request
  ) {
    return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorDTO> handleValidation(
      MethodArgumentNotValidException exception,
      HttpServletRequest request
  ) {
    String message = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .findFirst()
        .orElse("Dados inválidos");

    return buildError(HttpStatus.BAD_REQUEST, message, request);
  }
}
