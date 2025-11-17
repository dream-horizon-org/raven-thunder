package com.dream11.thunder.api.service;

import io.reactivex.rxjava3.core.Single;
import java.util.Set;

/**
 * Client to resolve cohorts for a user used in CTA eligibility checks.
 */
public interface UserCohortsClient {

  /**
   * Returns all cohorts for the given user.
   */
  Single<Set<String>> findAllCohorts(Long userId);
}
