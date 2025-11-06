package com.dream11.thunder.api.service.cohort;

import static com.dream11.thunder.api.constant.Constants.USER_COHORTS_BREAKER;

import com.dream11.thunder.api.config.AppConfig;
import com.dream11.thunder.api.service.UserCohortsClient;
import com.dream11.thunder.api.util.SDUICircuitBreaker;
import com.dream11.thunder.core.service.WebHttpClient;
import com.google.inject.Inject;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.operator.CircuitBreakerOperator;
import io.reactivex.rxjava3.Single;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UserCohortsClientImpl implements UserCohortsClient {

  private final String host;
  private final Integer port;
  private final Long timeout;
  private final WebHttpClient httpClient;
  private final SDUICircuitBreaker sduiCircuitBreaker;

  private static final String FIND_ALL_COHORTS_URI = "/user-cohort/realtime?userId=%s";

  @Inject
  public UserCohortsClientImpl(
      AppConfig appConfig, WebHttpClient httpClient, SDUICircuitBreaker circuitBreaker) {
    this.host = appConfig.getUserCohorts().getHost();
    this.port = appConfig.getUserCohorts().getPort();
    this.timeout = appConfig.getUserCohorts().getTimeout().getMs();
    this.httpClient = httpClient;
    this.sduiCircuitBreaker = circuitBreaker;
  }

  @Override
  @Deprecated
  public Single<Set<String>> findAllCohorts(Long userId) {
    // Deprecated: Always return "all" cohort for all users
    // User cohorts functionality has been deprecated
    log.debug("User cohorts deprecated - returning 'all' for user: {}", userId);
    return Single.just(Set.of("all"));
  }
}

@Data
class FindAllCohortsResponse {

  private List<String> data;
}
