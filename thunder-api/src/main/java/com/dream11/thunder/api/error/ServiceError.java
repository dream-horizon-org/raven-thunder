package com.dream11.thunder.api.error;

import com.dream11.rest.exception.RestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum ServiceError implements RestError {
  SERVICE_UNKNOWN_EXCEPTION("thunder-api-UNKNOWN-EXCEPTION", "Something went wrong", 500);

  final String errorCode;
  final String errorMessage;
  final int httpStatusCode;
}
