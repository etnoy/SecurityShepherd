package org.owasp.securityshepherd.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class GenericResponse {
  private String message;
  private String error;

  public GenericResponse(List<ObjectError> allErrors, String error) {
    this.error = error;
    String temp = allErrors.stream().map(e -> {
      if (e instanceof FieldError) {
        return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\""
            + e.getDefaultMessage() + "\"}";
      } else {
        return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\""
            + e.getDefaultMessage() + "\"}";
      }
    }).collect(Collectors.joining(","));
    this.message = "[" + temp + "]";
  }

  public GenericResponse(final String message) {
    super();
    this.message = message;
  }

  public GenericResponse(final String message, final String error) {
    super();
    this.message = message;
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public void setError(final String error) {
    this.error = error;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

}
