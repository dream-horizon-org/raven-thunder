package com.dream11.thunder.admin.util;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for validating CTA operations related to behaviour tags.
 */
@Slf4j
public final class CTAValidationHelper {

  private CTAValidationHelper() {
    // Utility class - prevent instantiation
  }

  /**
   * Validates that linked CTAs have valid statuses for creating a new behaviour tag.
   * Only DRAFT or PAUSED CTAs can be linked to new behaviour tags.
   *
   * @param allCTAs all CTAs for the tenant
   * @param linkedCtaIds the set of CTA IDs to be linked
   * @param behaviourTagName the name of the behaviour tag (for logging)
   * @throws DefinedException if any linked CTA has an invalid status
   */
  public static void validateCTAsForNewBehaviourTag(
      Map<Long, CTA> allCTAs, Set<String> linkedCtaIds, String behaviourTagName) {
    List<String> invalidCTANames = findInvalidCTAsForNewBehaviourTag(allCTAs, linkedCtaIds);

    if (!invalidCTANames.isEmpty()) {
      log.info(
          "Error in Behaviour tags linking for BT : {} , ctas {}",
          behaviourTagName,
          invalidCTANames);
      throw new DefinedException(
          ErrorEntity.INVALID_BEHAVIOUR_TAG_CREATION,
          "Only Draft/Paused CTAs can be attached to new Behaviour tags. CTAs with invalid statuses: "
              + invalidCTANames);
    }
  }

  /**
   * Validates that linked CTAs have valid statuses for updating a behaviour tag.
   * Only DRAFT or PAUSED CTAs can be linked during updates.
   *
   * @param allCTAs all CTAs for the tenant
   * @param linkedCtaIds the set of CTA IDs to be linked
   * @param behaviourTagName the name of the behaviour tag (for logging)
   * @throws DefinedException if any linked CTA has an invalid status
   */
  public static void validateCTAsForBehaviourTagUpdate(
      Map<Long, CTA> allCTAs, Set<String> linkedCtaIds, String behaviourTagName) {
    List<String> invalidCTANames = findInvalidCTAsForUpdate(allCTAs, linkedCtaIds);

    if (!invalidCTANames.isEmpty()) {
      log.info(
          "Error in Behaviour tags linking for BT : {} , ctas {}",
          behaviourTagName,
          invalidCTANames);
      throw new DefinedException(
          ErrorEntity.INVALID_BEHAVIOUR_TAG_UPDATION,
          "Only Draft/Paused CTAs can be attached to new Behaviour tags. CTAs with invalid statuses: "
              + invalidCTANames);
    }
  }

  /**
   * Finds CTAs with invalid statuses for creating a new behaviour tag.
   *
   * @param allCTAs all CTAs for the tenant
   * @param linkedCtaIds the set of CTA IDs to be linked
   * @return list of invalid CTA names
   */
  private static List<String> findInvalidCTAsForNewBehaviourTag(
      Map<Long, CTA> allCTAs, Set<String> linkedCtaIds) {
    return findInvalidCTAs(
        allCTAs, linkedCtaIds, CTAStatusValidator::isInvalidForNewBehaviourTagLink);
  }

  /**
   * Finds CTAs with invalid statuses for updating a behaviour tag.
   *
   * @param allCTAs all CTAs for the tenant
   * @param linkedCtaIds the set of CTA IDs to be linked
   * @return list of invalid CTA names
   */
  private static List<String> findInvalidCTAsForUpdate(
      Map<Long, CTA> allCTAs, Set<String> linkedCtaIds) {
    return findInvalidCTAs(
        allCTAs, linkedCtaIds, CTAStatusValidator::isInvalidForBehaviourTagUpdate);
  }

  /**
   * Generic method to find CTAs with invalid statuses based on a validation function.
   *
   * @param allCTAs all CTAs for the tenant
   * @param linkedCtaIds the set of CTA IDs to be linked
   * @param statusValidator function to validate CTA status
   * @return list of invalid CTA names
   */
  private static List<String> findInvalidCTAs(
      Map<Long, CTA> allCTAs,
      Set<String> linkedCtaIds,
      Function<CTAStatus, Boolean> statusValidator) {
    List<String> invalidCTANames = new ArrayList<>();

    allCTAs.values().forEach(
        cta -> {
          if (linkedCtaIds.contains(cta.getId().toString())
              && statusValidator.apply(cta.getCtaStatus())) {
            invalidCTANames.add(cta.getName());
          }
        });

    return invalidCTANames;
  }
}

