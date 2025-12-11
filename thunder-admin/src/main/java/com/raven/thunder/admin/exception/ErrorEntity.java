package com.raven.thunder.admin.exception;

import lombok.Getter;

@Getter
public enum ErrorEntity {
  CTA_CREATION_ERROR("Error while creating CTA", "UNPROCESSABLE_ENTITY", 422),
  CTA_UPDATE_ERROR("Error while updating CTA", "UNPROCESSABLE_ENTITY", 422),
  CTA_UPDATE_NAME_ERROR("CTA name cannot be changed", "UNPROCESSABLE_ENTITY", 422),
  NO_SUCH_CTA("no CTA exists for input ctaId", "UNPROCESSABLE_ENTITY", 422),
  NO_SUCH_BEHAVIOUR_TAG("no BehaviourTag exists for given name", "UNPROCESSABLE_ENTITY", 404),
  INVALID_BEHAVIOUR_TAG_CREATION(
      "Request body param(s) is/are invalid", "INVALID_BEHAVIOUR_TAG_CREATION", 400),
  INVALID_BEHAVIOUR_TAG_UPDATION(
      "Request body param(s) is/are invalid", "INVALID_BEHAVIOUR_TAG_UPDATION", 400),
  CTA_EXPIRED("CTA expired, ctaValidTill is less than current time", "UNPROCESSABLE_ENTITY", 422),
  INVALID_STATUS_UPDATE("This status update is invalid", "UNPROCESSABLE_ENTITY", 422),
  INVALID_START_TIME(
      "The start time of the CTA is either null or past", "UNPROCESSABLE_ENTITY", 422),
  INVALID_REQUEST_BODY(
      "Request body param(s) is/are missing/invalid", "BT_INVALID_REQUEST_BODY", 400),
  CTA_UPDATE_NOT_ALLOWED(
      "Edit not permitted for the current CTA status", "CTA_UPDATE_NOT_ALLOWED", 403),
  INVALID_CTA_TIME(
      "CTA start/end time should not be less than current time", "INVALID_CTA_TIME", 400),
  INVALID_CTA_END_TIME(
      "CTA end time should not be less than start time", "INVALID_CTA_END_TIME", 400),
  NAME_CHANGE_NOT_PERMITTED("CTA name cannot be modified", "NAME_CHANGE_NOT_PERMITTED", 403),
  DUPLICATE_NAME_NOT_PERMITTED(
      "CTA name cannot be duplicated", "DUPLICATE_NAME_NOT_PERMITTED", 403),
  TENANT_NOT_ALLOWED(
      "Tenant not authorised to access/create resource", "TENANT_NOT_AUTHORISED", 403);

  private final String cause;
  private final String errorCode;
  private final int httpStatusCode;

  ErrorEntity(String cause, String errorCode, int httpStatusCode) {
    this.cause = cause;
    this.errorCode = errorCode;
    this.httpStatusCode = httpStatusCode;
  }
}
