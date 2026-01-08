package com.raven.thunder.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cohort service configuration class. Contains HTTP client settings for cohort service. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CohortConfig {

  @JsonProperty("url")
  private String url;

  @JsonProperty("connection-pool-size")
  private Integer connectionPoolSize;

  @JsonProperty("max-pool-size")
  private Integer maxPoolSize;

  @JsonProperty("keep-alive")
  private Boolean keepAlive;

  @JsonProperty("keep-alive-timeout")
  private Integer keepAliveTimeout;

  @JsonProperty("connect-timeout")
  private Integer connectTimeout;

  @JsonProperty("idle-timeout")
  private Integer idleTimeout;

  @JsonProperty("max-wait-queue-size")
  private Integer maxWaitQueueSize;
}
