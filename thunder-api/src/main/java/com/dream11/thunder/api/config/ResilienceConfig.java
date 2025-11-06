package com.dream11.thunder.api.config;

import lombok.Data;

@Data
public class ResilienceConfig {
  private int failureRateThreshold;
  private int slowCallRateThreshold;
  private int waitDurationInOpenState;
  private int slowCallDurationThreshold;
  private int permittedNumberOfCallsInHalfOpenState;
  private int minimumNumberOfCalls;
  private int slidingWindowSize;
}
