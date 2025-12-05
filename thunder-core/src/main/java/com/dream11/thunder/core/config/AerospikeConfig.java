package com.dream11.thunder.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Aerospike configuration class. Contains all Aerospike connection settings. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AerospikeConfig {

  @JsonProperty("host")
  private String host;

  @JsonProperty("user-data-namespace")
  private String userDataNamespace;

  @JsonProperty("admin-data-namespace")
  private String adminDataNamespace;

  @JsonProperty("port")
  private Integer port;

  @JsonProperty("socket-timeout")
  private Integer socketTimeout;

  @JsonProperty("total-timeout")
  private Integer totalTimeout;

  @JsonProperty("max-connections")
  private Integer maxConnections;

  @JsonProperty("bulk-read-socket-timeout")
  private Integer bulkReadSocketTimeout;

  // Helper methods to convert Integer timeouts to Interval (for compatibility with old code)
  public Interval getSocketTimeoutInterval() {
    return new Interval(socketTimeout != null ? socketTimeout.longValue() : 5000L);
  }

  public Interval getTotalTimeoutInterval() {
    return new Interval(totalTimeout != null ? totalTimeout.longValue() : 10000L);
  }

  public Interval getBulkReadSocketTimeoutInterval() {
    return new Interval(bulkReadSocketTimeout != null ? bulkReadSocketTimeout.longValue() : 10000L);
  }
}
