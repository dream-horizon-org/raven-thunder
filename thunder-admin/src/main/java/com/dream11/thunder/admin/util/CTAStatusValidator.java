package com.dream11.thunder.admin.util;

import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;

/** Utility class for validating CTA status transitions and status checks. */
public final class CTAStatusValidator {

  private CTAStatusValidator() {
    // Utility class - prevent instantiation
  }

  /**
   * Validates if a CTA can transition to PAUSED status. Only LIVE or SCHEDULED CTAs can be paused.
   *
   * @param cta the CTA to validate
   * @return true if the CTA can be paused, false otherwise
   */
  public static boolean canPause(CTA cta) {
    return cta.getCtaStatus() == CTAStatus.LIVE || cta.getCtaStatus() == CTAStatus.SCHEDULED;
  }

  /**
   * Validates if a CTA can transition to LIVE status. Only DRAFT or PAUSED CTAs can be made live.
   *
   * @param cta the CTA to validate
   * @return true if the CTA can be made live, false otherwise
   */
  public static boolean canMakeLive(CTA cta) {
    return cta.getCtaStatus() == CTAStatus.DRAFT || cta.getCtaStatus() == CTAStatus.PAUSED;
  }

  /**
   * Validates if a CTA can transition to SCHEDULED status. Only DRAFT or PAUSED CTAs can be
   * scheduled.
   *
   * @param cta the CTA to validate
   * @return true if the CTA can be scheduled, false otherwise
   */
  public static boolean canSchedule(CTA cta) {
    return cta.getCtaStatus() == CTAStatus.DRAFT || cta.getCtaStatus() == CTAStatus.PAUSED;
  }

  /**
   * Validates if a CTA can transition to CONCLUDED status. Only PAUSED or LIVE CTAs can be
   * concluded.
   *
   * @param cta the CTA to validate
   * @return true if the CTA can be concluded, false otherwise
   */
  public static boolean canConclude(CTA cta) {
    return cta.getCtaStatus() == CTAStatus.PAUSED || cta.getCtaStatus() == CTAStatus.LIVE;
  }

  /**
   * Validates if a CTA can transition to TERMINATED status. Only PAUSED, LIVE, or DRAFT CTAs can be
   * terminated.
   *
   * @param cta the CTA to validate
   * @return true if the CTA can be terminated, false otherwise
   */
  public static boolean canTerminate(CTA cta) {
    return cta.getCtaStatus() == CTAStatus.PAUSED
        || cta.getCtaStatus() == CTAStatus.LIVE
        || cta.getCtaStatus() == CTAStatus.DRAFT;
  }

  /**
   * Checks if a CTA status is invalid for linking to a new behaviour tag. Only DRAFT or PAUSED CTAs
   * can be linked to new behaviour tags.
   *
   * @param status the CTA status to check
   * @return true if the status is invalid for linking, false otherwise
   */
  public static boolean isInvalidForNewBehaviourTagLink(CTAStatus status) {
    return status == CTAStatus.LIVE
        || status == CTAStatus.SCHEDULED
        || status == CTAStatus.CONCLUDED
        || status == CTAStatus.TERMINATED;
  }

  /**
   * Checks if a CTA status is invalid for updating behaviour tag links. Only DRAFT or PAUSED CTAs
   * can be linked during updates.
   *
   * @param status the CTA status to check
   * @return true if the status is invalid for linking, false otherwise
   */
  public static boolean isInvalidForBehaviourTagUpdate(CTAStatus status) {
    return status == CTAStatus.LIVE || status == CTAStatus.SCHEDULED;
  }

  /**
   * Checks if a CTA status is valid for linking to behaviour tags. Only DRAFT or PAUSED CTAs are
   * valid.
   *
   * @param status the CTA status to check
   * @return true if the status is valid for linking, false otherwise
   */
  public static boolean isValidForBehaviourTagLink(CTAStatus status) {
    return status == CTAStatus.DRAFT || status == CTAStatus.PAUSED;
  }

  /**
   * Validates if a start time is valid for scheduling. Start time must not be null and must be in
   * the future.
   *
   * @param startTime the start time to validate
   * @return true if the start time is valid, false otherwise
   */
  public static boolean isValidStartTimeForScheduling(Long startTime) {
    return startTime != null && startTime >= System.currentTimeMillis();
  }
}
