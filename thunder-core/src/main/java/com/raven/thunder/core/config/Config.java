package com.raven.thunder.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main configuration class for the Thunder application. This class provides a type-safe way to
 * access application configuration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {

  @JsonProperty("server")
  private ServerConfig server;

  @JsonProperty("aerospike")
  private AerospikeConfig aerospike;
}
