package com.dream11.thunder.api.service.cohort;

import com.dream11.thunder.api.service.UserCohortsClient;
import com.google.inject.Singleton;
import io.reactivex.rxjava3.core.Single;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Deprecated implementation of UserCohortsClient.
 * Always returns Set.of("all") as cohorts are deprecated.
 */
@Slf4j
@Singleton
@Deprecated
public class UserCohortsClientImpl implements UserCohortsClient {

  @Override
  public Single<Set<String>> findAllCohorts(Long userId) {
    log.warn("User cohorts are deprecated. Returning default cohort 'all' for userId: {}", userId);
    return Single.just(Set.of("all"));
  }
}
