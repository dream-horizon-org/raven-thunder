package com.dream11.thunder.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Server configuration class. Contains HTTP server settings. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {

  @JsonProperty("host")
  private String host;

  @JsonProperty("port")
  private Integer port;

  @JsonProperty("instances")
  private Integer instances;

  @JsonProperty("compression-supported")
  private Boolean compressionSupported;

  @JsonProperty("idle-timeout")
  private Integer idleTimeout;
}
