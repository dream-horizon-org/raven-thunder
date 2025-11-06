package com.dream11.thunder.api.exception;

import com.dream11.rest.exception.RestException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefinedException extends RestException {

  public DefinedException(ErrorEntity errorEntity) {
    super(errorEntity.getCause(), errorEntity.getError(), errorEntity.getHttpStatusCode());
  }

  public DefinedException(ErrorEntity errorEntity, Object... args) {
    super(
        String.format(errorEntity.getCause(), args),
        errorEntity.getError(),
        errorEntity.getHttpStatusCode());
  }
}
