package com.dream11.thunder.api.util;

import com.dream11.thunder.api.config.ResilienceConfig;
import com.google.inject.Inject;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vertx.serviceproxy.ServiceException;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SDUICircuitBreaker {
  private final ResilienceConfig resilienceConfig;

  private static final CircuitBreakerRegistry circuitBreakerRegistry =
      CircuitBreakerRegistry.ofDefaults();

  private final Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();

  private void createCircuitBreaker(String name, CircuitBreakerConfig circuitBreakerConfig) {
    circuitBreakerMap.putIfAbsent(
        name, circuitBreakerRegistry.circuitBreaker(name, circuitBreakerConfig));
  }

  public CircuitBreaker getCircuitBreaker(String name) {
    createCircuitBreaker(name, buildCircuitBreakerConfig());
    return circuitBreakerMap.get(name);
  }

  public CircuitBreakerConfig buildCircuitBreakerConfig() {
    return CircuitBreakerConfig.custom()
        .failureRateThreshold(resilienceConfig.getFailureRateThreshold())
        .slowCallRateThreshold(resilienceConfig.getSlowCallRateThreshold())
        .waitDurationInOpenState(Duration.ofMillis(resilienceConfig.getWaitDurationInOpenState()))
        .slowCallDurationThreshold(
            Duration.ofMillis(resilienceConfig.getSlowCallDurationThreshold()))
        .permittedNumberOfCallsInHalfOpenState(
            resilienceConfig.getPermittedNumberOfCallsInHalfOpenState())
        .minimumNumberOfCalls(resilienceConfig.getMinimumNumberOfCalls())
        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
        .slidingWindowSize(resilienceConfig.getSlidingWindowSize())
        .recordExceptions(
            IOException.class,
            TimeoutException.class,
            Exception.class, // add custom exception
            ServiceException.class)
        .build();
  }

  public static void printCircuitBreakerState(CircuitBreaker circuitBreaker) {
    log.info(
        "CircuitBreaker: {} | Successful call count: {} | Failed call count: {} | Failure rate %:{} | State: {}",
        circuitBreaker.getName(),
        circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(),
        circuitBreaker.getMetrics().getNumberOfFailedCalls(),
        circuitBreaker.getMetrics().getFailureRate(),
        circuitBreaker.getState());
  }
}
