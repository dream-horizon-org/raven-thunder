package com.dream11.thunder.api.util;

import com.dream11.thunder.core.model.CTA;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Utility class for filtering CTAs based on various criteria. */
public final class CTAFilterUtil {

  private CTAFilterUtil() {
    // Utility class - prevent instantiation
  }

  /**
   * Filters CTAs by tenant ID.
   *
   * @param ctas the map of CTAs to filter
   * @param tenantId the tenant ID to filter by
   * @return filtered map of CTAs for the tenant
   */
  public static Map<Long, CTA> filterByTenant(Map<Long, CTA> ctas, String tenantId) {
    return ctas.entrySet().stream()
        .filter(e -> tenantId.equals(e.getValue().getTenantId()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Filters active CTAs that are eligible for a user based on cohort eligibility rules. A CTA is
   * eligible if: - User has at least one cohort in the CTA's include list - User has no cohorts in
   * the CTA's exclude list
   *
   * @param tenantId the tenant ID
   * @param userCohorts the set of user cohorts
   * @param activeCTAs the map of active CTAs
   * @return filtered map of eligible CTAs
   */
  public static Map<Long, CTA> filterEligibleCTAs(
      String tenantId, Set<String> userCohorts, Map<Long, CTA> activeCTAs) {
    return activeCTAs.entrySet().stream()
        .filter(e -> tenantId.equals(e.getValue().getTenantId()))
        .filter(entry -> isEligibleByCohorts(entry.getValue(), userCohorts))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Checks if a CTA is eligible for a user based on cohort rules.
   *
   * @param cta the CTA to check
   * @param userCohorts the user's cohorts
   * @return true if eligible, false otherwise
   */
  private static boolean isEligibleByCohorts(CTA cta, Set<String> userCohorts) {
    if (cta.getRule() == null || cta.getRule().getCohortEligibility() == null) {
      return false;
    }

    // Check if user has at least one cohort in include list
    boolean hasIncludedCohort =
        cta.getRule().getCohortEligibility().getIncludes().stream().anyMatch(userCohorts::contains);

    if (!hasIncludedCohort) {
      return false;
    }

    // Check if user has any cohort in exclude list
    boolean hasExcludedCohort =
        cta.getRule().getCohortEligibility().getExcludes().stream().anyMatch(userCohorts::contains);

    return !hasExcludedCohort;
  }
}
