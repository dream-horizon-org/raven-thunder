package com.dream11.thunder.api.exception;

import com.dream11.rest.io.Error;
import lombok.Getter;

@Getter
public enum ErrorEntity {
  NO_SUCH_CTA(
      "no CTA exists for input ctaId",
      Error.of("UNPROCESSABLE_ENTITY", "no CTA exists for input ctaId"),
      422),
  NO_SUCH_BEHAVIOUR_TAG(
      "no BehaviourTag exists for given name",
      Error.of("UNPROCESSABLE_ENTITY", "no BehaviourTag exists for given name"),
      404),
  CTA_EXPIRED(
      "CTA expired, ctaValidTill is less than current time",
      Error.of("UNPROCESSABLE_ENTITY", "CTA expired, ctaValidTill is less than current time"),
      422);

  private final String cause;
  private final Error error;
  private final int httpStatusCode;

  ErrorEntity(String cause, Error error, int httpStatusCode) {
    this.cause = cause;
    this.error = error;
    this.httpStatusCode = httpStatusCode;
  }
}
