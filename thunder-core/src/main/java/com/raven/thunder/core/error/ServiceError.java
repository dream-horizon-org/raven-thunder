package com.raven.thunder.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum ServiceError {
  NUDGE_PREVIEW_NOT_FOUND_EXCEPTION(
      "NUDGE_PREVIEW_NOT_FOUND_EXCEPTION", "Nudge preview not found", 400),
  NUDGE_PREVIEW_TEMPLATE_NOT_FOUND_EXCEPTION(
      "NUDGE_PREVIEW_TEMPLATE_NOT_FOUND_EXCEPTION", "Nudge preview template not found", 400),
  NUDGE_PREVIEW_ID_CANNOT_BE_EMPTY(
      "NUDGE_PREVIEW_TEMPLATE_NOT_FOUND_EXCEPTION", "id cannot be null or empty", 400),
  NUDGE_PREVIEW_TEMPLATE_CANNOT_BE_EMPTY(
      "NUDGE_PREVIEW_TEMPLATE_NOT_FOUND_EXCEPTION", "template cannot be null or empty", 400),
  TENANT_NOT_AUTHORISED(
      "TENANT_NOT_AUTHORISED_EXCEPTION", "tenant not authorised for the resource", 400);

  private final String errorCode;
  private final String errorMessage;
  private final int httpStatusCode;
}
