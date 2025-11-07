package com.dream11.thunder.admin.exception;

import lombok.Getter;

/**
 * Custom exception for Thunder Admin service.
 * Contains error code, message and HTTP status code.
 */
@Getter
public class DefinedException extends RuntimeException {
  
  private final String errorCode;
  private final int httpStatusCode;

  public DefinedException(ErrorEntity errorEntity) {
    super(errorEntity.getCause());
    this.errorCode = errorEntity.getErrorCode();
    this.httpStatusCode = errorEntity.getHttpStatusCode();
  }

  public DefinedException(ErrorEntity errorEntity, String cause) {
    super(cause);
    this.errorCode = errorEntity.getErrorCode();
    this.httpStatusCode = errorEntity.getHttpStatusCode();
  }

  public DefinedException(ErrorEntity errorEntity, Object... args) {
    super(String.format(errorEntity.getCause(), args));
    this.errorCode = errorEntity.getErrorCode();
    this.httpStatusCode = errorEntity.getHttpStatusCode();
  }
}

