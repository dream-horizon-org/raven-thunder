package com.dream11.thunder.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Server configuration class.
 * Contains HTTP server settings.
 */
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
    
    @JsonProperty("compressionSupported")
    private Boolean compressionSupported;
    
    @JsonProperty("idleTimeout")
    private Integer idleTimeout;
}



